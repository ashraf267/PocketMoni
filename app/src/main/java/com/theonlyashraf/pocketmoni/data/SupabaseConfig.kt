package com.theonlyashraf.pocketmoni.data

/**
 * Supabase project configuration parameters.
 *
 * Replace [SUPABASE_URL] and [SUPABASE_ANON_KEY] with your live Supabase project credentials.
 * If left with default placeholders, the application automatically runs in zero-config
 * simulated event stream mode, ensuring 100% demo reliability anywhere.
 */
object SupabaseConfig {
    const val SUPABASE_URL: String = "https://czmlizoyprvevlleiwwk.supabase.co"
    const val SUPABASE_ANON_KEY: String = "sb_publishable_W1-cJAl5vJSfl3ISfXr_aA_8Q5Hn2Z-"
    const val DEFAULT_MERCHANT_ID: String = "MERCHANT_ETR_2067"

    val isConfigured: Boolean
        get() = SUPABASE_URL.startsWith("https://") && SUPABASE_ANON_KEY.length > 20
}
