# 시퀀스 다이어그램

- 잔액 충전
    
    ```mermaid
    sequenceDiagram
        participant User
        participant API
        participant Balance
        participant Database
    
        User ->> API: POST 잔액 충전 (userId, amount)
        alt 유효한 사용자
            API ->> Database: 사용자 확인
            Database -->> API: 사용자 존재 여부
            alt 사용자 존재
                alt 금액이 유효
                    API ->> Balance: 충전 처리 (userId, amount)
                    Balance ->> Database: 사용자 잔액 조회
                    Database -->> Balance: 잔액 정보
                    Balance ->> Database: 잔액 업데이트 (+amount)
                    Database -->> Balance: 업데이트 성공
                    Balance -->> API: 충전 성공 (최종 잔액)
                    API -->> User: 정상 응답 (최종 잔액)
                else 금액이 유효하지 않음
                    API -->> User: 오류 응답 ("유효하지 않은 금액")
                end
            else 사용자 존재하지 않음
                API -->> User: 오류 응답 ("유효하지 않은 사용자")
            end
        else 유효하지 않은 요청
            API -->> User: 오류 응답 ("잘못된 요청")
        end
    
    ```
    
- 인기 상품 조회
    
    ```mermaid
    sequenceDiagram
        participant User
        participant API
        participant ProductService
        participant Database
    
        User ->> API: GET /api/products/top
        API ->> ProductService: 인기 상품 조회 요청
        ProductService ->> Database: 최근 3일간 주문 데이터 분석
        Database -->> ProductService: 인기 상품 데이터
        ProductService -->> API: 인기 상품 정보
        API -->> User: 인기 상품 조회 응답
    
    ```
    
- 주문/결제
    
    ```mermaid
    sequenceDiagram
        participant User
        participant API
        participant OrderService
        participant BalanceService
        participant ProductService
        participant CouponService
        participant Database
        participant DataPlatform
    
        User ->> API: POST /api/orders (userId, products, couponId)
        API ->> OrderService: 주문 처리 요청
        OrderService ->> BalanceService: 사용자 잔액 확인
        BalanceService ->> Database: 사용자 잔액 조회
        Database -->> BalanceService: 잔액 데이터
        alt 잔액 부족
            BalanceService -->> OrderService: 잔액 부족
            OrderService -->> API: 오류 응답 ("잔액 부족")
            API -->> User: 오류 응답 ("잔액 부족")
        else 잔액 충분
            OrderService ->> ProductService: 상품 재고 확인
            ProductService ->> Database: 상품 재고 조회
            Database -->> ProductService: 재고 정보
            alt 재고 부족
                ProductService -->> OrderService: 재고 부족
                OrderService -->> API: 오류 응답 ("재고 부족")
                API -->> User: 오류 응답 ("재고 부족")
            else 재고 충분
                OrderService ->> CouponService: 쿠폰 유효성 확인
                CouponService ->> Database: 쿠폰 데이터 조회
                Database -->> CouponService: 쿠폰 데이터
                alt 쿠폰 유효하지 않음
                    CouponService -->> OrderService: 유효하지 않은 쿠폰
                    OrderService -->> API: 오류 응답 ("쿠폰 오류")
                    API -->> User: 오류 응답 ("유효하지 않은 쿠폰")
                else 쿠폰 유효
                    CouponService -->> OrderService: 쿠폰 사용 가능
                    OrderService ->> BalanceService: 잔액 차감
                    BalanceService ->> Database: 잔액 업데이트
                    Database -->> BalanceService: 업데이트 성공
                    OrderService ->> ProductService: 재고 차감
                    ProductService ->> Database: 재고 업데이트
                    Database -->> ProductService: 업데이트 성공
                    OrderService ->> Database: 주문 데이터 저장
                    Database -->> OrderService: 저장 성공
                    OrderService ->> DataPlatform: 주문 데이터 전송
                    DataPlatform -->> OrderService: 전송 성공
                    OrderService -->> API: 주문 처리 완료
                    API -->> User: 성공 응답 (주문 정보)
                end
            end
        end
    
    ```
    
- 잔액 조회
    
    ```mermaid
    sequenceDiagram
        participant User
        participant API
        participant BalanceService
        participant Database
    
        User ->> API: GET /api/balance/{userId}
        API ->> BalanceService: 잔액 조회 요청 (userId)
        BalanceService ->> Database: 사용자 잔액 조회
        Database -->> BalanceService: 잔액 정보
        BalanceService -->> API: 잔액 데이터
        API -->> User: 잔액 조회 응답
    
    ```
    
- 쿠폰 조회
    
    ```mermaid
    sequenceDiagram
        participant User
        participant API
        participant CouponService
        participant Database
    
        User ->> API: GET /api/coupon/users/{userId}
        API ->> CouponService: 사용자 쿠폰 조회 요청
        CouponService ->> Database: 사용자 보유 쿠폰 조회
        Database -->> CouponService: 쿠폰 데이터 반환
        CouponService -->> API: 쿠폰 데이터
        API -->> User: 쿠폰 조회 응답
    
    ```
    
- 쿠폰 발급
    
    ```mermaid
    sequenceDiagram
        participant User
        participant API
        participant CouponService
        participant Redis
        participant Database
    
        User ->> API: POST /api/coupon/issue (userId, couponId)
        API ->> CouponService: 쿠폰 발급 요청
        CouponService ->> Redis: 쿠폰 재고 확인
        alt 쿠폰 재고 있음
            Redis -->> CouponService: 재고 차감 성공
            CouponService ->> Database: 사용자 쿠폰 저장
            Database -->> CouponService: 저장 성공
            CouponService -->> API: 발급 성공
            API -->> User: 쿠폰 발급 완료 응답
        else 쿠폰 재고 부족
            Redis -->> CouponService: 재고 부족
            CouponService -->> API: 발급 실패 (재고 부족)
            API -->> User: 오류 응답 ("쿠폰 재고 부족")
        end
    
    ```
    
- 상품 조회
    
    ```mermaid
    sequenceDiagram
        participant User
        participant API
        participant Product
        participant Database
    
        User ->> API: GET /api/products?page=0&size=10&keyword=prdname
        API ->> ProductService: 요청 정보 전달 (page, size, keyword)
        ProductService ->> Database: 상품 목록 조회 (검색어 포함)
        Database -->> Product: 상품 목록 및 페이징 정보
        ProductService -->> API: 상품 데이터
        API -->> User: 상품 목록 응답
    
    ```
    
- squence
    
    ```mermaid
    sequenceDiagram
        participant User
        participant API Gateway
        participant AuthService
        participant BalanceService
        participant ProductService
        participant CouponService
        participant OrderService
        participant Database
        participant Cache
        participant DataPlatform
    
        %% Step 1: User sends request
        User ->> API Gateway: 요청 (상품 조회, 주문, 잔액 충전 등)
    
        %% Step 2: Authentication
        API Gateway ->> AuthService: 인증 및 권한 확인 (JWT 토큰 등)
        AuthService -->> API Gateway: 인증 결과
    
        alt 인증 성공
            %% Step 3: Route to appropriate service
            API Gateway ->> BalanceService: 잔액 충전/조회 요청 (Balance 관련 API)
            API Gateway ->> ProductService: 상품 조회 요청 (상품 관련 API)
            API Gateway ->> CouponService: 쿠폰 발급/조회 요청 (쿠폰 관련 API)
            API Gateway ->> OrderService: 주문 요청 (주문 및 결제 관련 API)
    
            %% Step 4: Service interaction
            par 잔액 충전/조회
                BalanceService ->> Database: 사용자 잔액 조회/충전
                Database -->> BalanceService: 잔액 정보/충전 결과
                BalanceService -->> API Gateway: 결과 반환
            and 상품 조회
                ProductService ->> Cache: 상품 목록 캐싱 데이터 확인
                alt 캐시 있음
                    Cache -->> ProductService: 캐시된 상품 데이터
                else 캐시 없음
                    ProductService ->> Database: 상품 목록 조회
                    Database -->> ProductService: 상품 데이터
                    ProductService ->> Cache: 상품 데이터 캐싱
                end
                ProductService -->> API Gateway: 상품 목록 반환
            and 쿠폰 발급/조회
                CouponService ->> Cache: 쿠폰 재고 확인 (Redis)
                alt 재고 있음
                    Cache -->> CouponService: 재고 차감 성공
                    CouponService ->> Database: 사용자 쿠폰 저장
                    Database -->> CouponService: 저장 성공
                    CouponService -->> API Gateway: 쿠폰 발급 성공
                else 재고 부족
                    Cache -->> CouponService: 재고 부족
                    CouponService -->> API Gateway: 발급 실패
                end
            and 주문 처리
                OrderService ->> BalanceService: 잔액 확인
                BalanceService ->> Database: 사용자 잔액 조회
                Database -->> BalanceService: 잔액 정보
                alt 잔액 충분
                    OrderService ->> ProductService: 재고 확인
                    ProductService ->> Database: 재고 조회
                    Database -->> ProductService: 재고 정보
                    alt 재고 충분
                        OrderService ->> CouponService: 쿠폰 유효성 확인
                        CouponService ->> Database: 쿠폰 데이터 조회
                        Database -->> CouponService: 쿠폰 정보
                        OrderService ->> BalanceService: 잔액 차감
                        BalanceService ->> Database: 잔액 업데이트
                        Database -->> BalanceService: 업데이트 성공
                        OrderService ->> ProductService: 재고 차감
                        ProductService ->> Database: 재고 업데이트
                        Database -->> ProductService: 업데이트 성공
                        OrderService ->> Database: 주문 데이터 저장
                        Database -->> OrderService: 저장 성공
                        OrderService ->> DataPlatform: 주문 데이터 전송
                        DataPlatform -->> OrderService: 전송 성공
                        OrderService -->> API Gateway: 주문 처리 성공
                    else 재고 부족
                        ProductService -->> OrderService: 재고 부족 오류
                    end
                else 잔액 부족
                    BalanceService -->> OrderService: 잔액 부족 오류
                end
            end
    
            %% Step 5: Return response to User
            API Gateway -->> User: 응답 반환
        else 인증 실패
            API Gateway -->> User: 인증 오류 응답
        end
    
    ```