# Lab Safety Management System (MVP)

## Roles
- ADMIN: manage everything (users, labs, equipment, reservations, trainings/exams, inspections, analytics)
- TEACHER: manage experiment projects; approve/reject reservations
- STUDENT: browse resources; submit reservations; take exams

## Modules
- Users (register/login, profile; admin CRUD users)
- Labs (admin CRUD)
- Equipment (admin CRUD; browse/search)
- Experiment Projects (teacher CRUD; student browse)
- Reservations (student submit; teacher approve/reject; admin overview)
- Training + Exam (MCQ only, record attempts/scores)
- Inspections (plan + report; simple closed loop)
- Analytics (basic charts)

## MVP Defaults (Source of truth)
### Reservation
- Time: startAt/endAt stored in UTC, overlap rule: [startAt, endAt) (endAt is exclusive)
- Status: PENDING / APPROVED / REJECTED / CANCELLED
- Only APPROVED reservations participate in conflict checks
- Conflicts:
    - A reservation cannot be APPROVED if it overlaps an existing APPROVED reservation for the same lab
    - If equipment is selected: reserve 1 unit; cannot be APPROVED if overlapping approved reservations exceed equipment.quantity
- Approval: any TEACHER can approve/reject; ADMIN can override

### Exam
- MCQ only; pass mark 70; unlimited attempts; passing is NOT required for reservation
- Store attempts with score and passed flag per user per course

### Inspections
- States: OPEN -> FIXING -> FIXED -> VERIFIED
- MVP: ADMIN creates plans and records reports (no separate INSPECTOR role)
- Report includes text findings + status

### Analytics (MVP)
- Counts only: reservations by status, top labs by approved reservations, equipment usage counts, training pass rate, inspections by status


