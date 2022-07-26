package pk.farimarwat.modernadmob

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import pk.farimarwat.modernadmob.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MobileAds.initialize(this)
        binding.myads
            .loadAd(this,"ca-app-pub-3940256099942544/1044960115",
            object : AdmobView.ModernAdmobListener {
                override fun onAdClicked() {

                }

                override fun onAdClosed() {

                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e("TEST",error.message)
                }

                override fun onAdImpression() {

                }

                override fun onAdLoaded() {
                    Log.e("TEST","Add Loaded")
                }

                override fun onAdOpened() {

                }


            })
    }
}