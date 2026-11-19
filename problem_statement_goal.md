# Problem Statement & Goal: Instant Credit Alert Notification Feature for eTranzact POS

## 1. Context & Background
- **Target App:** eTranzact's POS / PocketMoni Merchant application running on Android terminals (such as the Topwise MP35P).
- **The Core User Pain Point:** Merchants and agents frequently experience delayed credit alert notifications when customers complete fund transfers. According to live user feedback (e.g., Google Play Store complaints from merchants), these delays cause severe operational friction, customer embarrassment, and trust loss during transactions.
- **Industry Benchmark:** Competitors like Moniepoint utilize instant acoustic notifications (a clear "ching/ding" sound effect) the exact moment a credit alert or settlement hits the agent's wallet.

---

## 2. Problem Statement
Agents using eTranzact’s merchant platform currently lack real-time acoustic feedback for incoming transfers. Because backend transaction confirmation can suffer from network latency or polling delays, merchants are forced to manually refresh or wait anxiously, leading to slowed queue times and disputed transactions. 

The goal is to design and build a lightweight, native Android proof-of-concept (PoC) service that listens for incoming transfer settlements in real-time and triggers an immediate, hard-coded audio notification (sound alert), eliminating transfer verification delay entirely.

---

## 3. End Goal & Deliverables
By the end of this sprint, the AI coding agent will help build a clean, production-ready vertical slice demonstrating:
1. **Real-Time Event Stream (Mocked via Supabase):** A lightweight database table `transactions` configured with Supabase Realtime WebSockets to simulate instant credit alert pushes.
2. **Foreground/Background Android Listener:** A native Android service/client that listens to incoming row insertions for a specific merchant ID.
3. **Instant Audio Trigger:** Upon receiving a transaction payload, the app instantly plays a loud, crisp notification sound (using local raw assets via `MediaPlayer` or `SoundPool`).
4. **Interactive Demo UI:** A clean 1-screen merchant dashboard featuring a **"Simulate Customer Transfer"** button that writes a dummy record to Supabase, instantly triggering the local audio alert within < 1 second.
5. **Zero-Production Risk:** Built cleanly using Kotlin/Clean Architecture principles so it can be packaged as a standalone APK and embedded or pitched directly to eTranzact recruiters as a working solution.