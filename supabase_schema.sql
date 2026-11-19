-- ====================================================================
-- Supabase Schema & Realtime Setup for eTranzact PocketMoni PoC
-- ====================================================================
-- Run this script in your Supabase Project SQL Editor:
-- Dashboard -> SQL Editor -> New Query -> Run
-- ====================================================================

-- 1. Create the `transactions` table
CREATE TABLE IF NOT EXISTS public.transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    merchant_id TEXT NOT NULL DEFAULT 'MERCHANT_ETR_1001',
    amount NUMERIC(12, 2) NOT NULL,
    sender_name TEXT NOT NULL,
    reference TEXT NOT NULL,
    timestamp TEXT NOT NULL DEFAULT to_char(now(), 'HH12:MI:SS AM'),
    type TEXT NOT NULL DEFAULT 'CREDIT',
    created_at TIMESTAMPTZ DEFAULT now()
);

-- 2. Enable Row Level Security (RLS) & Create Public Access Policy for PoC
ALTER TABLE public.transactions ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "Allow public read and insert access" ON public.transactions;

CREATE POLICY "Allow public read and insert access"
ON public.transactions
FOR ALL
USING (true)
WITH CHECK (true);

-- 3. Enable Supabase Realtime Replication on the `transactions` table
-- (Crucial for WebSocket live pushes to Android POS terminals)
ALTER PUBLICATION supabase_realtime ADD TABLE public.transactions;
