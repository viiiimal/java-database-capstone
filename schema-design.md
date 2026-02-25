## MySQL Database Design

### Table: patients
- id: INT, Primary Key, Auto Increment
- first_name: VARCHAR(100), Not Null
- last_name: VARCHAR(100), Not Null
- email: VARCHAR(150), Unique, Not Null
- phone: VARCHAR(20), Not Null
- date_of_birth: DATE
- created_at: TIMESTAMP, Default CURRENT_TIMESTAMP

### Table: doctors
- id: INT, Primary Key, Auto Increment
- first_name: VARCHAR(100), Not Null
- last_name: VARCHAR(100), Not Null
- specialization: VARCHAR(150), Not Null
- email: VARCHAR(150), Unique, Not Null
- phone: VARCHAR(20)
- availability_status: VARCHAR(50)

### Table: admin
- id: INT, Primary Key, Auto Increment
- username: VARCHAR(100), Unique, Not Null
- password_hash: VARCHAR(255), Not Null
- role: VARCHAR(50), Default 'ADMIN'

### Table: appointments
- id: INT, Primary Key, Auto Increment
- doctor_id: INT, Foreign Key → doctors(id)
- patient_id: INT, Foreign Key → patients(id)
- appointment_time: DATETIME, Not Null
- status: INT (0 = Scheduled, 1 = Completed, 2 = Cancelled)
- created_at: TIMESTAMP, Default CURRENT_TIMESTAMP

### Table: clinic_locations
- id: INT, Primary Key, Auto Increment
- name: VARCHAR(150), Not Null
- address: VARCHAR(255), Not Null
- phone: VARCHAR(20)

### Table: payments
- id: INT, Primary Key, Auto Increment
- appointment_id: INT, Foreign Key → appointments(id)
- amount: DECIMAL(10,2), Not Null
- payment_method: VARCHAR(50)
- payment_status: VARCHAR(50)
- payment_date: TIMESTAMP

## MongoDB Collection Design

### Collection: prescriptions
```json
{
  "_id": "ObjectId('64abc123456')",
  "patientId": 101,
  "doctorId": 22,
  "appointmentId": 501,
  "medications": [
    {
      "name": "Paracetamol",
      "dosage": "500mg",
      "frequency": "Every 6 hours",
      "duration": "5 days"
    }
  ],
  "doctorNotes": "Patient should rest and stay hydrated.",
  "refillCount": 1,
  "createdAt": "2026-02-26T10:30:00Z",
  "pharmacy": {
    "name": "City Pharmacy",
    "location": "Main Street"
  }
}
