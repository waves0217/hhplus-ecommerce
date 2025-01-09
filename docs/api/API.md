# API 명세서

### 상품 조회

1. 판매 중인 상품의 목록을 조회합니다.
- Request
    - URL :  /api/products
    - Method : GET
    - Query Parameters
        - page : 요청할 페이지 번호
        - size : 한 페이지당 데이터 개수
        - keyword : 검색어(상품명)
    
    예시) /api/products?page=0&size=10&keyword=prdname 
    
- Response
    - 성공
    
    ```json
    {
      "result": "SUCCESS",
      "data": {
        "page": {
          "number": 0,
          "size": 10,
          "totalPages": 5,
          "totalElements": 50
        },
        "items": [
          {
            "productId": 1,
            "productName": "product name 1",
            "price": 10000,
            "stock": 100
          },
          {
            "productId": 2,
            "productName": "product name 2",
            "price": 20000,
            "stock": 200
          }
        ]
      }
    }
    ```
    
    - 실패
    
    ```json
    {
      "result": "ERROR",
      "error": {
        "code": 400,
        "message": "검색 파라미터가 잘못되었습니다.",
      }
    }
    ```
    
    ```json
    {
      "result": "ERROR",
      "error": {
        "code": 500,
        "message": "상품 조회 중 오류가 발생했습니다."
      }
    }
    ```
    

### 쿠폰 발급

1. 쿠폰을 발급 받습니다.
2. 재고 소진 시 발급 불가.
- Request
    - URL :  /api/coupon/issue
    - Method : POST
    
    ```json
    {
      "userId": 1,
      "couponId": 22
    }
    ```
    
- Response
    - 성공
    
    ```json
    {
      "result": "SUCCESS",
      "data": {
        "userCouponId": 123,
        "userId": 1,
        "couponId": 22,
        "couponName": "New Year Discount",
        
        "issuedAt": "2024-12-31T12:00:00",
        "expiredAt": "2025-01-31T12:00:00",
        "status": "ISSUED"
      }
    }
    ```
    
    - 실패
    
    ```json
    {
      "result": "ERROR",
      "error": {
        "code": 400,
        "message": "이미 쿠폰을 발급받았습니다."
      }
    }
    ```
    
    ```json
    {
      "result": "ERROR",
      "error": {
        "code": 400,
        "message": "쿠폰 재고가 모두 소진되었습니다."
      }
    }
    ```
    
    ```json
    {
      "result": "ERROR",
      "error": {
        "code": 500,
        "message": "쿠폰 발급 처리 중 오류가 발생했습니다."
      }
    }
    ```
    

### 쿠폰 조회

1. 사용자가 보유 중인 쿠폰을 조회
- Request
    - URL :  /api/coupon/users/{userId}
    - Method : GET
    - Path Variable **:** userId(사용자ID)
- Response
    - 성공
    
    ```json
    {
      "result": "SUCCESS",
      "data": {
    	  "userId":1,
    	  "coupon": [
    		  {
    	      "userCouponId": 123,
    	      "couponName": "10% 할인"
    	      "discountRate": 10,
    	      "discountType": "P",
    		    "couponId": 22,
    		    "expiredAt": "2026-01-01T00:00:00",
    		    "useYn": "N"
    		  },
    		  {
    	      "userCouponId": 456,
    	      "couponName": "5천원 할인"
    	      "discountRate": 5000,
    	      "discountType": "F",
    		    "couponId": 44,
    		    "expiredAt": "2026-01-01T00:00:00",
    		    "useYn": "N"			  
    		  }
    	  ]
      }
    }
    ```
    
    - 실패
    
    ```json
    {
      "result": "ERROR",
      "error": {
        "code": 400,
        "message": "존재하지 않는 사용자입니다.",
      }
    }
    ```
    
    ```json
    {
      "result": "ERROR",
      "error": {
        "code": 500,
        "message": "쿠폰 조회 중 오류가 발생했습니다."
      }
    }
    ```
    

### 잔액 충전/조회

1. 잔액 충전 - 사용자의 잔액을 충전합니다
- Request
    - URL :  /api/balance/charge
    - Method : POST
    
    ```json
    {
      "userId": 1,
      "amount": 10000
    }
    ```
    
- Response
    - 성공
    
    ```json
    {
      "result": "SUCCESS",
      "data": {
        "userId": 1,
        "balance": 20000
      }
    }
    ```
    
    - 실패
    
    ```json
    {
      "result": "ERROR",
      "error": {
        "code": 400,
        "message": "충전 금액이 잘못되었습니다."
      }
    }
    ```
    
    ```json
    {
      "result": "ERROR",
      "error": {
        "code": 500,
        "message": "잔액 충전 중 오류가 발생했습니다."
      }
    }
    ```
    

1. 잔액 조회 - 사용자의 잔액을 조회합니다
- Request
    - URL :  /api/balance/{userId}
    - Method : GET
    - Path Variable **:** userId(사용자ID)
- Response
    - 성공
    
    ```json
    {
      "result": "SUCCESS",
      "data": {
        "userId": 1,
        "balance": 20000
      }
    }
    ```
    
    - 실패
    
    ```json
    {
      "result": "ERROR",
      "error": {
        "code": 400,
        "message": "사용자가 존재하지 않습니다."
      }
    }
    ```
    
    ```json
    {
      "result": "ERROR",
      "error": {
        "code": 500,
        "message": "잔액 조회중 오류가 발생했습니다."
      }
    }
    ```
    

### 주문/결제

1. 상품을 주문하고 유저 잔액을 차감하여 결제, 쿠폰 사용 시 할인.
- Request
    - URL :  /api/orders
    - Method : POST
    
    ```json
    {
      "userId": 1,
      "products": [
        {
          "productId": 1,
          "quantity": 2
        },
        {
          "productId": 3,
          "quantity": 1
        }
      ],
      "couponId": 10
    }
    ```
    
- Response
    - 성공
    
    ```json
    {
      "result": "SUCCESS",
      "data": {
        "orderId": 123,
        "totalPrice": 45000,
        "discountedPrice": 40000,
        "remainingBalance": 10000,
        "products": [
          {
            "productId": 1,
            "productName": "Product 1",
            "quantity": 2,
            "price": 20000
          },
          {
            "productId": 3,
            "productName": "Product 3",
            "quantity": 1,
            "price": 5000
          }
        ],
        "coupon": {
          "couponId": 10,
          "name": "10% Discount",
          "discountAmount": 5000,
          "discountType": "PERCENT"
        }
      }
    }
    
    ```
    
    - 실패
    
    ```json
    {
      "result": "ERROR",
      "error": {
        "code": 400,
        "message": "잔액이 부족합니다.",
      }
    }
    ```
    
    ```json
    {
      "result": "ERROR",
      "error": {
        "code": 400,
        "message": "유효하지 않은 쿠폰입니다.",
      }
    }
    ```
    
    ```json
    {
      "result": "ERROR",
      "error": {
        "code": 400,
        "message": "상품의 재고가 부족합니다.",
        "data": {
          "productId": 1,
          "requestedQuantity": 5,
          "availableStock": 2
        }
      }
    }
    ```
    
    ```json
    {
      "result": "ERROR",
      "error": {
        "code": 500,
        "message": "주문 처리 중 오류가 발생했습니다.",
      }
    }
    ```
    

### 인기 상품 조회

1. 최근 3일간 판매량 기준으로 상위 5개 상품을 조회
- Request
    - URL :  /api/products/top
    - Method : GET
    - Query Parameters: 없음 (추가 옵션이 필요한 경우 추후 고려)
- Response
    - 성공
    
    ```json
    {
      "result": "SUCCESS",
      "data": {
        "period": {
          "startDate": "2025-01-01",
          "endDate": "2025-01-03"
        },
        "topProducts": [
          {
            "productId": 1,
            "productName": "Product A",
            "totalSold": 50,
            "price": 10000
          },
          {
            "productId": 2,
            "productName": "Product B",
            "totalSold": 40,
            "price": 20000
          },
          {
            "productId": 3,
            "productName": "Product C",
            "totalSold": 30,
            "price": 15000
          }
        ]
      }
    }
    
    ```
    
    - 실패
    
    ```json
    {
      "result": "SUCCESS",
      "data": {
        "period": {
          "startDate": "2025-01-01",
          "endDate": "2025-01-03"
        },
        "topProducts": [],
        "message": "최근 3일간 판매된 상품이 없습니다."
      }
    }
    
    ```
    
    ```json
    {
      "result": "ERROR",
      "error": {
        "code": 500,
        "message": "인기 상품 데이터를 가져오는 중 오류가 발생했습니다.",
      }
    }
    
    ```