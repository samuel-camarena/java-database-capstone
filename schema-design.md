# Datastructure Design for Smart Clinic Management System

## 1. MySQL Database Design

### 1.1. Tables list:

1. admins
2. doctors
3. patients
4. appointments
5. doctor_available_times

### 1.2. Table definitions:

#### 1.2.1. Table: admins

- **id:** BIG_INT, NOT NULL, AUTO INCREMENT, PRIMARY KEY
- **user_name:** VARCHAR(100), NOT NULL, UNIQUE
- **password:** VARCHAR(20), NOT NULL
- **email:** VARCHAR(255), UNIQUE, NOT NULL
- **creation_at:** TIMESTAMP, Default CURRENT_TIMESTAMP

#### 1.2.2. Table: doctors

- **id:** BIG_INT, NOT NULL, AUTO INCREMENT, PRIMARY KEY,
- **name:** VARCHAR(100), NOT NULL
- **password:** VARCHAR(20), NOT NULL
- **email:** VARCHAR(255), UNIQUE, NOT NULL, UNIQUE KEY
- **address:** VARCHAR(255), NOT NULL
- **phone:** VARCHAR(10), UNIQUE
- **speciality:** VARCHAR(50)
- **available_times:** VARCHAR(255)
- **years_of_experience:** INT, NOT NULL, CONSTRAINT (years_of_experience >= 0 and years_of_experience <= 99)
- **rating:** DOUBLE, NOT NULL, CONSTRAINT (rating >= 1 and rating <= 5)
- **creation_at:** TIMESTAMP, Default CURRENT_TIMESTAMP 

#### 1.2.3. Table: patients

- **id:** BIG_INT, NOT NULL, AUTO INCREMENT, PRIMARY KEY
- **name:** VARCHAR(100), NOT NULL
- **password:** VARCHAR(20), NOT NULL
- **address:** VARCHAR(255), NOT NULL
- **date_of_birth:** DATE, NOT NULL
- **sex:** VARCHAR(20), NOT NULL
- **email:** VARCHAR(255), NOT NULL, UNIQUE KEY
- **phone:** VARCHAR(10), NOT NULL
- **emergency_contact:** VARCHAR(10), NOT NULL
- **creation_at:** TIMESTAMP, Default CURRENT_TIMESTAMP
- 
#### 1.2.4. Table: appointments

- **id:** BIG_INT, NOT NULL, AUTO INCREMENT, PRIMARY KEY
- **doctor_id:** BIG_INT, NOT NULL, FOREIGN KEY -> doctors (id)
- **patient_id:** BIG_INT, NOT NULL, FOREIGN KEY -> patients (id)
- **appointment_date:** DATETIME, NOT NULL
- **status:** INT, NOT NULL, (0 = Scheduled, 1 = Completed, 2 = Cancelled)
- **reason_for_visiting:** VARCHAR(200), NOT NULL
- **notes:** VARCHAR(200), DEFAULT NULL

#### 1.2.5. Table: doctor_available_times

- **doctor_id:** BIG_INT, NOT NULL, FOREIGN KEY -> doctors (id)
- **available_times:** VARCHAR(255), DEFAULT NULL

## 2. MongoDB Collection Design

### 2.1. Collections:

1. prescriptions
2. notes 
3. feedbacks

### 2.2. Collection definitions:

#### 2.2.1. Collection: prescriptions
```json
{
  "_id": "ObjectId('')",
  "patientName": "VARCHAR()",
  "appointmentId": "INT",
  "medication": "VARCHAR()",
  "dosage": "VARCHAR(min = 3, max = 20)",
  "doctorNotesID": "-> (max = 200)",
  "refillCount": "INT",
  "pharmacyID": "ObjectPharmacy"
}
```

#### 2.2.2. Collection: notes
```json
{
  "_id": "INT, Primary Key, Auto Increment",
  "doctorID": "INT, Foreign Key → Doctors(ID)",
  "title": "VARCHAR(100)",
  "description": "VARCHAR(1000)",
  "creationAt": "DATETIME, NOT NULL"
}
```

#### 2.2.3. Collection: feedbacks
```json
{
  "_id": "INT, Primary Key, Auto Increment",
  "doctorID": "INT, Foreign Key → Doctors(ID)",
  "title": "VARCHAR(100)",
  "description": "VARCHAR(1000)",
  "creationAt": "DATETIME, NOT NULL"
}
```
