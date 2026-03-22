# Expense Tracker – Frontend

React + Vite frontend for the [expense-tracker](https://github.com/your-org/expense-tracker-monolith) backend API.

## Features

- **Auth**: Register, login (JWT), logout
- **Expenses**: List, create, edit, delete; filter by date range and category
- **Categories**: List, create, delete
- **Reports**: Monthly totals by year; category breakdown by date range

## Prerequisites

- Node 18+
- Backend running at `http://localhost:8080` (see repo root / `expense-tracker`)

## Setup

```bash
npm install
```

## Development

Start the backend (from `expense-tracker`):

```bash
cd expense-tracker && ./mvnw spring-boot:run
```

Start the frontend (from this directory):

```bash
npm run dev
```

The app will be at **http://localhost:5173**. API calls go to `/api/*` and are proxied to `http://localhost:8080` by Vite, so no CORS setup is needed in dev.

## Production build

```bash
npm run build
```

For production, serve the `dist` folder and set `VITE_API_URL` to your backend URL (e.g. `https://api.example.com`) so the app can call the API. The backend must allow your frontend origin in CORS.

## Environment

| Variable         | Description |
|------------------|-------------|
| `VITE_API_URL`   | Optional. Backend base URL. Empty = use relative `/api` (works with Vite proxy in dev). |
