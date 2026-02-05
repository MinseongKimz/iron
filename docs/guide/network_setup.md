# 네트워크 및 방화벽 설정 가이드

OCI (Oracle Cloud Infrastructure) 또는 기타 서버 환경에서 외부 접근을 허용하기 위한 포트 정보입니다.

## 1. 필수 포트 (Inbound Rules)

| 서비스명 | 포트 | 용도 | 비고 |
| :--- | :--- | :--- | :--- |
| **Frontend (Next.js)** | `3000` | 웹 브라우저 접근 | 사용자가 웹앱에 접속하기 위해 필요 |
| **Backend (Spring Boot)** | `8080` | API 서버 | 프론트엔드가 백엔드와 통신하기 위해 필요 |
| **SSH** | `22` | 터미널 접속 | (이미 뚫려있을 수 있음) 서버 관리용 |

## 2. 선택적 포트

| 서비스명 | 포트 | 용도 | 보안 주의사항 |
| :--- | :--- | :--- | :--- |
| **PostgreSQL** | `5432` | DB 직접 접속 | **보안 위험**: 외부 접근을 허용하면 해킹 위험이 높으므로, 로컬 접속(localhost)이나 VPN/SSH 터널링을 권장합니다. 테스트 목적이 아니라면 닫아두세요. |

## 3. 설정 방법 (OCI 예시)
1. OCI 콘솔 > Networking > Virtual Cloud Networks (VCN).
2. 해당 서브넷의 **Security List** 선택.
3. **Add Ingress Rule** 클릭.
   - Source CIDR: `0.0.0.0/0` (전체 허용) 또는 특정 IP.
   - IP Protocol: `TCP`.
   - Destination Port Range: `3000, 8080`.
4. (Ubuntu의 경우) 서버 내부 방화벽(`iptables` 또는 `ufw`)에서도 허용 필요할 수 있음.
   ```bash
   sudo ufw allow 3000/tcp
   sudo ufw allow 8080/tcp
   sudo ufw reload
   ```
