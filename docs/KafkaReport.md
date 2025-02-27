# Kafka 개요 및 주요 개념 정리

## 1. 개요

Kafka는 대용량 데이터를 빠르고 안정적으로 처리할 수 있는 분산 메시지 큐 시스템이다. 다양한 시스템 간의 데이터 송수신을 돕는 **메시지 브로커(Message Broker)** 역할을 하며, 높은 처리량과 확장성을 제공한다.

Kafka는 **실시간 로그 처리, 데이터 스트리밍, 이벤트 기반 아키텍처** 등의 다양한 분야에서 활용되며, 메시지를 **Producer**가 생성하고 **Consumer**가 읽어가는 방식으로 동작한다.

## 2. 주요 개념

### 2.1 Producer & Consumer (생산자 & 소비자)

- **Producer(생산자)**: Kafka로 데이터를 전송하는 역할을 한다.
- **Consumer(소비자)**: Kafka에서 데이터를 가져와 처리하는 역할을 한다.
- Producer가 보낸 메시지는 특정 **Topic(토픽)** 에 저장되며, Consumer는 해당 Topic을 구독하여 데이터를 가져간다.

### 2.2 Broker (브로커)

- **Kafka의 서버 역할을 하는 요소**로, 메시지를 저장하고 관리하는 기능을 수행한다.
- 하나의 Kafka 클러스터에는 여러 개의 Broker가 존재할 수 있으며, 각각 고유한 ID를 가진다.
- **Producer가 전송한 메시지를 저장하고 Consumer가 가져갈 수 있도록 관리**한다.

### 2.3 Message (메시지)

- **Kafka에서 다루는 데이터 단위**이며, JSON, String, Binary 등 다양한 형식으로 저장될 수 있다.
- 각 메시지는 **Key, Value, Offset, Timestamp** 등의 정보를 포함한다.

### 2.4 Topic & Partition (토픽 & 파티션)

- **Topic(토픽)**: 메시지를 저장하는 논리적 구분 단위이며, 특정 목적의 데이터를 모아놓은 공간이다.
- **Partition(파티션)**: 하나의 Topic을 여러 개로 나누어 저장하는 물리적 단위이다.
  - 메시지를 여러 Broker에 분산 저장하여 성능을 높이고, 병렬 처리가 가능하도록 한다.
  - 각 메시지는 **Offset** 값을 가지며, Consumer는 이 Offset을 기반으로 데이터를 순차적으로 가져간다.

### 2.5 Consumer Group (컨슈머 그룹)

- 같은 역할을 하는 여러 개의 Consumer를 하나의 그룹으로 묶은 개념이다.
- **Kafka는 동일한 Consumer Group 내에서 같은 메시지를 한 번만 전달**하여 중복 처리를 방지한다.
- Consumer Group이 여러 개 있을 경우, 서로 다른 Group은 동일한 메시지를 각각 받을 수 있다.

### 2.6 Rebalancing (리밸런싱)

- Consumer의 개수가 변하거나 장애가 발생하면 **메시지의 할당을 자동으로 조정하는 과정**이다.
- 새로운 Consumer가 추가되거나 기존 Consumer가 종료되면, Kafka가 **Partition을 재할당하여 균형을 맞춘다.**

### 2.7 Cluster (클러스터)

- 여러 개의 Kafka **Broker(서버)** 들이 모여 하나의 클러스터를 구성한다.
- 클러스터를 통해 데이터 분산 저장 및 확장성을 제공할 수 있다.
- 일반적으로 대규모 시스템에서는 **다수의 Broker가 하나의 클러스터로 동작**하며, 이를 통해 부하 분산과 장애 복구가 가능하다.

### 2.8 Replication (복제)

- **데이터의 안정성을 보장하기 위한 기능**으로, 메시지를 여러 개의 Broker에 복제하여 저장한다.
- **Replication Factor** 값을 설정하면, 해당 값만큼 메시지가 복제된다.
- 예를 들어, Replication Factor가 3이라면 동일한 메시지가 3개의 Broker에 저장된다.
- **Leader-Follower 구조**를 사용하며, 한 개의 **Leader** Partition이 쓰기 작업을 담당하고, 나머지 **Follower** Partition이 Leader를 복제하여 장애 발생 시 Leader를 대체할 수 있도록 한다.

## 3. Kafka의 데이터 흐름 정리

1. Producer가 메시지를 특정 **Topic** 에 전송한다.
2. Kafka의 **Broker** 가 메시지를 **Partition** 에 저장한다.
3. Consumer가 **Consumer Group** 을 통해 메시지를 가져간다.
4. 필요하면 **Replication** 을 통해 메시지를 여러 서버에 복제한다.
5. Consumer 개수가 변경되면 **Rebalancing** 이 발생하여 Partition을 재할당한다.

## 4. 결론

Kafka는 **대용량 데이터를 안정적이고 빠르게 처리할 수 있도록 설계된 분산 메시징 시스템**이다.

Kafka는 높은 성능과 확장성을 제공하며, **로그 처리, 실시간 데이터 분석** 등의 다양한 분야에서 활용될 수 있다.