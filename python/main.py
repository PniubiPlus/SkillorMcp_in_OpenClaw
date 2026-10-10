"""Demo 入口"""
from detector import detect, dispose
import mock_mcp_server as mcp

if __name__ == "__main__":
    print("=== 恶意充值检测 Demo (Python) ===")
    results = detect()
    for r in results:
        print(r)
    dispose(results)
    print("封禁名单:", mcp.BANNED)
