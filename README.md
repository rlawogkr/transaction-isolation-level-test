1. testReadCommitted()

실행 흐름:

**threadA**가 먼저 시작하여 ID가 1인 계좌를 읽고 (balance = 1000), +100을 한 후 저장하여 balance = 1100이 된다.
**threadB**는 500ms 후에 실행되어 READ_COMMITTED 격리 수준에 따라 threadA의 커밋된 변경 사항을 읽을 수 있다. 
따라서 threadB는 balance = 1100에서 다시 +100을 하여 balance = 1200으로 저장한다.
결과값:

1200 (1000 + 100 + 100)


2. testRepeatableRead()

실행 흐름:

**threadA**가 ID가 2인 계좌를 읽고 (balance = 2000), +100을 하여 balance = 2100으로 업데이트하지만, 아직 커밋하지 않는다.
**threadB**는 500ms 후에 실행되어 데이터를 조회하지만, threadA가 아직 커밋하지 않았기 때문에 변경되지 않은 초기값인 balance = 2000을 읽게 된다.
**threadA**는 이후에 커밋하여 최종적으로 balance를 2100으로 업데이트한다.
결과값:

threadB는 2000을 확인하고, 최종적으로 balance는 2100이 된다.


3. testSerializable()

실행 흐름:

**threadA**가 ID가 3인 계좌를 읽고 (balance = 3000), +100을 하여 balance = 3100으로 업데이트하고 커밋합니다.
**threadB**는 500ms 후에 실행되며, threadA의 트랜잭션이 끝난 후 동일한 계좌를 읽고 +100을 하여 balance = 3200으로 저장합니다.
결과값:

3200 (3000 + 100 + 100)
