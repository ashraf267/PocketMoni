# PocketMoni - Instant Credit Alert Notification PoC

A lightweight, native Android Proof-of-Concept (PoC) demonstrating **Instant Acoustic Credit Alerts** for **eTranzact POS / PocketMoni Merchant** terminals.

---

## 1. Context & Background
- **Target App:** eTranzact's POS / PocketMoni Merchant application running on Android terminals (such as the Topwise MP35P).
- **The Core User Pain Point:** Merchants and agents frequently experience delayed credit alert notifications when customers complete bank transfers. According to live user feedback (e.g., Google Play Store reviews from PocketMoni merchants), these delays cause severe operational friction, customer embarrassment, and queue delays during transactions.
- **Industry Benchmark:** Competitors like Moniepoint utilize instant acoustic notifications (a clear "cha-ching" sound effect) the exact moment a credit alert or settlement hits the agent's wallet.

---

## 2. Problem Statement
Agents using eTranzact’s merchant platform currently lack real-time acoustic feedback for incoming transfers. Because backend transaction confirmation can suffer from network latency or polling delays, merchants are forced to manually refresh or wait anxiously, leading to slowed queue times and disputed transactions.

This project designs and builds a production-ready native Android vertical slice that listens for incoming transfer settlements in real-time and triggers an immediate sound alert (sub-50ms acoustic feedback), eliminating transfer verification delay entirely.

---

## 3. Architecture & Key Features

```
[ Supabase DB / Realtime WebSockets ]
             │
             ▼ (Postgres INSERT Stream)
┌─────────────────────────────────────────┐
│     TransactionRepository               │
└────────────────────┬────────────────────┘
                     │
                     ▼ (StateFlow)
┌─────────────────────────────────────────┐
│     MerchantDashboardViewModel          │
└──────────────┬──────────────────┬───────┘
               │                  │
               ▼                  ▼
┌──────────────────────┐  ┌──────────────────────┐
│  CreditSoundPlayer   │  │ MerchantDashboardUI  │
│  (SoundPool < 50ms)  │  │ (Jetpack Compose)    │
└──────────────────────┘  └──────────────────────┘
```

1. **Sub-50ms Low-Latency Audio Engine (`CreditSoundPlayer`):** Uses Android's native `SoundPool` API with `USAGE_NOTIFICATION_EVENT` priority to pre-load audio buffers directly into RAM, outperforming `MediaPlayer` overhead.
2. **Real-Time Push Stream (`TransactionRepository`):** Subscribes to Supabase PostgreSQL Realtime WebSockets (`postgresChangeFlow`) for instant push updates.
3. **Zero-Config Fallback Stream:** Includes an in-memory event stream mode ensuring 100% demo reliability anywhere even if credentials or offline networks occur.
4. **Interactive Merchant Dashboard UI (`MerchantDashboardScreen`):** A clean Jetpack Compose UI with wallet balance updates, live credit feed, and a **"Simulate Customer Transfer"** button for live recruiter demonstrations.

---

## 4. Tech Stack
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose & Material 3
- **Architecture:** Clean Architecture + ViewModel + StateFlow
- **Realtime Networking:** Supabase Kotlin SDK 3.x (`realtime-kt`, `postgrest-kt`) + Ktor Client OkHttp
- **Audio Engine:** Android `SoundPool`

---

## 5. Setup & Running

### **A. Database Setup (Supabase)**
1. Open your [Supabase Dashboard](https://supabase.com/dashboard) ➔ **SQL Editor**.
2. Run the provided script [`supabase_schema.sql`](supabase_schema.sql):

```sql
CREATE TABLE IF NOT EXISTS public.transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    merchant_id TEXT NOT NULL DEFAULT 'MERCHANT_ETR_2067',
    amount NUMERIC(12, 2) NOT NULL,
    sender_name TEXT NOT NULL,
    reference TEXT NOT NULL,
    timestamp TEXT NOT NULL DEFAULT to_char(now(), 'HH12:MI:SS AM'),
    type TEXT NOT NULL DEFAULT 'CREDIT',
    created_at TIMESTAMPTZ DEFAULT now()
);

ALTER TABLE public.transactions ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Allow public read and insert access" ON public.transactions FOR ALL USING (true) WITH CHECK (true);
ALTER PUBLICATION supabase_realtime ADD TABLE public.transactions;
```

### **B. Application Configuration**
Open `SupabaseConfig.kt` (`app/src/main/java/com/theonlyashraf/pocketmoni/data/SupabaseConfig.kt`) and set your Supabase Project URL and Anon Key:

```kotlin
object SupabaseConfig {
    const val SUPABASE_URL: String = "https://your-project-id.supabase.co"
    const val SUPABASE_ANON_KEY: String = "YOUR_SUPABASE_ANON_KEY"
    const val DEFAULT_MERCHANT_ID: String = "MERCHANT_ETR_2067"
}
```

---

## 6. Verification & Demo
1. Build and run the app on an Android device or emulator.
2. Tap **"Simulate Transfer & Play Alert 🔔"**.
3. Hear the instant credit chime and watch the transaction appear in real-time both on the screen and in your Supabase `transactions` table!

---

## 7. Demo & User Feedback Proof

### 💬 Google Play Store Merchant Feedback
Below is the live user review from a PocketMoni merchant on the Google Play Store requesting instant acoustic notifications similar to Moniepoint:

<p align="center">
  <img src="https://github.com/user-attachments/assets/d721cf1c-1d5e-42ae-ba51-862444ecb90c" alt="PocketMoni Play Store Customer Complaint" width="700"/>
</p>

### 🎥 Live PoC Demo Video
Demonstration of instant credit audio alert and real-time WebSocket sync with Supabase:

https://github.com/user-attachments/assets/f5790227-a786-40d2-a52a-6f6cd0879c26
