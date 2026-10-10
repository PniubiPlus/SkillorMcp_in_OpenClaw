package main

import "fmt"

// 简易 Mock 内部数据 MCP
type Recharge struct {
	UserID   string
	OrderID  string
	Amount   float64
	Refunded bool
	DeviceID string
	IP       string
}

type Bonus struct {
	UserID   string
	Bonus    int
	Consumed int
}

var recharges = []Recharge{
	{"user_001", "ord_001", 6, false, "dev_A", "1.1.1.1"},
	{"user_002", "ord_002", 6, true, "dev_B", "2.2.2.2"},
	{"user_003", "ord_003", 1, false, "dev_B", "2.2.2.2"},
	{"user_003", "ord_004", 1, false, "dev_B", "2.2.2.2"},
	{"user_003", "ord_005", 1, false, "dev_B", "2.2.2.2"},
	{"user_004", "ord_006", 100, false, "dev_C", "3.3.3.3"},
}

var bonuses = []Bonus{
	{"user_001", 1000, 100},
	{"user_002", 1000, 1000},
	{"user_003", 3000, 2900},
	{"user_004", 5000, 500},
}

var banned = map[string]string{}

func QueryRecharges() []Recharge { return recharges }
func QueryBonus() []Bonus        { return bonuses }
func BanUser(uid, reason string) { banned[uid] = reason }
func RestrictBonus(uid string)   { fmt.Println("restrict bonus:", uid) }

type Result struct {
	UserID string
	Score  int
	Hits   []string
	Level  string
}

func Detect() []Result {
	bonusMap := map[string]Bonus{}
	for _, b := range bonuses {
		bonusMap[b.UserID] = b
	}
	// 设备聚合
	devUsers := map[string]map[string]bool{}
	for _, r := range recharges {
		if devUsers[r.DeviceID] == nil {
			devUsers[r.DeviceID] = map[string]bool{}
		}
		devUsers[r.DeviceID][r.UserID] = true
	}
	var out []Result
	for uid, b := range bonusMap {
		score := 0
		var hits []string
		var orders []Recharge
		for _, r := range recharges {
			if r.UserID == uid {
				orders = append(orders, r)
			}
		}
		hasRefund := false
		smallCnt := 0
		for _, o := range orders {
			if o.Refunded {
				hasRefund = true
			}
			if o.Amount <= 1 {
				smallCnt++
			}
		}
		if hasRefund && float64(b.Consumed) >= float64(b.Bonus)*0.9 {
			score += 60
			hits = append(hits, "refund_abuse")
		}
		if len(orders) >= 3 && smallCnt == len(orders) {
			score += 50
			hits = append(hits, "small_amount_brushing")
		}
		for dev, users := range devUsers {
			if users[uid] && len(users) >= 2 {
				score += 30
				hits = append(hits, "device_sharing:"+dev)
				break
			}
		}
		rate := float64(b.Consumed) / float64(max(1, b.Bonus))
		if rate >= 0.9 && len(orders) <= 2 {
			score += 20
			hits = append(hits, "fast_consume")
		}
		level := "pass"
		switch {
		case score >= 85:
			level = "ban"
		case score >= 50:
			level = "restrict"
		case score >= 30:
			level = "watch"
		}
		out = append(out, Result{uid, score, hits, level})
	}
	return out
}

func max(a, b int) int {
	if a > b {
		return a
	}
	return b
}

func main() {
	fmt.Println("=== 恶意充值检测 Demo (Go) ===")
	results := Detect()
	for _, r := range results {
		fmt.Printf("%+v\n", r)
		if r.Level == "ban" {
			BanUser(r.UserID, fmt.Sprintf("score=%d", r.Score))
		} else if r.Level == "restrict" {
			RestrictBonus(r.UserID)
		}
	}
	fmt.Println("封禁名单:", banned)
}
