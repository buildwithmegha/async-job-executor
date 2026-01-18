# Async Job Executor (Spring Boot + Multithreading)

A Spring Boot backend project that demonstrates core and advanced Java multithreading concepts by implementing an asynchronous job processing system (similar to background workers in real production systems).

## 🚀 Tech Stack
- Java 17
- Spring Boot 3
- Spring Data JPA
- H2 Database
- ThreadPoolExecutor / BlockingQueue
- Postman for testing

---

## ✅ Features
### Job APIs
- Create job (enqueue for async execution)
- Fetch job status/details
- List all jobs
- Cancel running job

### Job Processing
- Producer–Consumer model using `BlockingQueue`
- Background Dispatcher thread
- Configurable `ThreadPoolExecutor`
- Job lifecycle:
    - `QUEUED → RUNNING → SUCCESS / FAILED`
    - retry state: `RETRYING`
    - cancellation: `CANCELLED`

### Reliability
- Automatic retry (max 3 retries)
- Exponential backoff retry strategy (1s → 2s → 4s)
- Cancellation supported using `Future.cancel(true)` with interruption handling

### Monitoring
- Executor metrics endpoint (active threads, queue size, completed tasks)

---

## 🧠 Multithreading Topics Covered
- Thread lifecycle & daemon thread
- ThreadPoolExecutor tuning (core/max threads, queue, rejection policy)
- Producer–Consumer using `BlockingQueue`
- Runnable / Callable
- Future cancellation + thread interruption
- ConcurrentHashMap (tracking running job futures)
- Race condition prevention & safe shared state handling

---

## 📌 API Endpoints

### 1) Create Job
**POST** `/jobs`

Request:
```json
{
  "jobType": "REPORT",
  "payload": "Generate report for userId=12"
}
```

Response:
```json
{
  "id": "a1b2c3d4-e5f6-g7h8-i9j0",
  "jobType": "REPORT",
  "payload": "Generate report for userId=12",
  "status": "QUEUED",
  "progress": 0,
  "retryCount": 0,
  "resultMessage": null,
  "errorMessage": null,
  "createdAt": "2023-05-17T12:34:56.789Z",
  "updatedAt": "2023-05-17T12:34:56.789Z"   
}   
```

### 2) Fetch Job Status
**GET** `/jobs/{jobId}` 

Response:   
```json
{
  "id": "a1b2c3d4-e5f6-g7h8-i9j0",
  "jobType": "REPORT",
  "payload": "Generate report for userId=12",
  "status": "SUCCESS",
  "progress": 100,
  "retryCount": 0,
  "resultMessage": "Report generated successfully",
  "errorMessage": null,
  "createdAt": "2023-05-17T12:34:56.789Z",
  "updatedAt": "2023-05-17T12:34:56.789Z"
}
``` 

### 3) List All Jobs
**GET** `/jobs` 

Response:   
```json 
[
  {
    "id": "a1b2c3d4-e5f6-g7h8-i9j0",
    "jobType": "REPORT",
    "payload": "Generate report for userId=12",
    "status": "SUCCESS",
    "progress": 100,
    "retryCount": 0,
    "resultMessage": "Report generated successfully",
    "errorMessage": null,
    "createdAt": "2023-05-17T12:34:56.789Z",
    "updatedAt": "2023-05-17T12:34:56.789Z"
  }     
]
}      
```         

### 4) Cancel Running Job   
**POST** `/jobs/{jobId}/cancel`              

Response:
```json
{
  "id": "a1b2c3d4-e5f6-g7h8-i9j0",
  "jobType": "REPORT",
  "payload": "Generate report for userId=12",
  "status": "CANCELLED",
  "progress": 0,
  "retryCount": 0,
  "resultMessage": null,
  "errorMessage": null,
  "createdAt": "2023-05-17T12:34:56.789Z",
  "updatedAt": "2023-05-17T12:34:56.789Z"
}
```

