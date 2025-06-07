# Datastructure Design for Smart Clinic Management System

## 1. MySQL Database Design

### Tables:

1. appointments
2. patients
3. doctors
4. admins
5. pharmacys
6. payments
7. documents

### Table definitions:

#### Table: appointments
- id: INT, PRIMARY KEY, AUTO INCREMENT
- doctor_id: INT, FOREIGN KEY → doctors(id)
- patient_id: INT, FOREIGN KEY → patients(id)
- appointment_date: DATETIME, NOT NULL
- duration: INT
- status: INT (0 = Scheduled, 1 = Completed, 2 = Cancelled)

#### Table: patients
- id: INT, PRIMARY KEY, AUTO INCREMENT
- user_name: VARCHAR(50), NOT NULL, UNIQUE
- password: VARCHAR(50), NOT NULL
- name: VARCHAR(100), NOT NULL
- address: VARCHAR(255), NOT NULL
- dob: DATE,
- sex: VARCHAR(20), NOT NULL
- email: VARCHAR(100), UNIQUE, NOT NULL
- phone: VARCHAR(20), UNIQUE
- creation_at: TIMESTAMP, Default CURRENT_TIMESTAMP

#### Table: doctors
- id: INT, PRIMARY KEY, AUTO INCREMENT
- user_name: VARCHAR(50), NOT NULL, UNIQUE
- password: VARCHAR(50), NOT NULL
- name: VARCHAR(100), NOT NULL
- address: VARCHAR(255), NOT NULL
- email: VARCHAR(100), UNIQUE, NOT NULL
- phone: VARCHAR(20), UNIQUE
- speciality: VARCHAR(50)
- available_times: VARCHAR(255)
- creation_at: TIMESTAMP, Default CURRENT_TIMESTAMP

#### Table: admins
- id: INT, PRIMARY KEY, AUTO INCREMENT
- user_name: VARCHAR(50), NOT NULL, UNIQUE
- password: VARCHAR(50), NOT NULL
- name: VARCHAR(100), NOT NULL 
- email: VARCHAR(100), UNIQUE, NOT NULL
- phone: VARCHAR(20), NOT NULL, UNIQUE
- creation_at: TIMESTAMP, Default CURRENT_TIMESTAMP

#### Table: pharmacys
- id: INT, PRIMARY KEY, AUTO INCREMENT
- name: VARCHAR(255), NOT NULL
- location: VARCHAR(255), NOT NULL

#### Table: payments
- id: INT, PRIMARY KEY, AUTO INCREMENT
- patient_id: INT, FOREIGN KEY → Patients(id)
- amount: DATETIME, NOT NULL
- date: DATETIME, NOT NULL

#### Table: documents
- id: INT, PRIMARY KEY, Auto Increment
- doctor_id: INT, FOREIGN KEY → doctors(id)
- title: VARCHAR(100)
- description: VARCHAR(1000)
- creation_At: DATETIME, NOT NULL

## 2. MongoDB Collection Design

### Collections:

1. prescriptions
2. notes 
3. feedbacks

### Collection definitions:

#### Collection: prescriptions
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

#### Collection: notes
```json
{
  "_id": "INT, Primary Key, Auto Increment",
  "doctorID": "INT, Foreign Key → Doctors(ID)",
  "title": "VARCHAR(100)",
  "description": "VARCHAR(1000)",
  "creationAt": "DATETIME, NOT NULL"
}
```

#### Collection: feedbacks
```json
{
  "_id": "INT, Primary Key, Auto Increment",
  "doctorID": "INT, Foreign Key → Doctors(ID)",
  "title": "VARCHAR(100)",
  "description": "VARCHAR(1000)",
  "creationAt": "DATETIME, NOT NULL"
}
```
