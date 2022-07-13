package pk.farimarwat.modernadmob

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

class AdmobView(context:Context, attrs:AttributeSet):RelativeLayout(context,attrs) {
    lateinit var mContext: Context
    private var colorprimary = COLOR_PRIMARY
    private var colorprimarydark = COLOR_PRIMARY_DARK
    private var colorprimarylight = 0
    private var colortextonprimary = 0
    private var colortextonprimaryDark = 0
    private var colortextonprimaryLight = 0
    private var adtype = AD_TYPE_MEDIUM
    private var mCustonTemplate = 0
    private var mAdview:NativeAdView? = null
    private var mListener:ModernAdmobListener? = null
    private var adLoader:AdLoader? = null

    //Ads fields
    lateinit var ta:TypedArray
    private var mAppIcon:ImageView? = null
    private var mLogo:ImageView? = null
    private var mHeadline:TextView? = null
    private var mAdBodyText:TextView? = null
    private var mStarRating:RatingBar? = null
    private var mMedia:MediaView? = null
    private var mImage:ImageView? = null
    private var mActionButton:Button? = null
    private var mAdvertiser:TextView? = null
    private var mPrice:TextView? = null
    private var mStore:TextView? = null
    private var mBadge:TextView? = null



    init {
        mContext = context
        ta = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.AdmobView,0,0
        )
        adtype = ta.getInteger(R.styleable.AdmobView_av_adtype, AD_TYPE_MEDIUM)
        mCustonTemplate = ta.getResourceId(R.styleable.AdmobView_av_customTemplate,0)
        if(mCustonTemplate > 0){
            val view = inflate(context,mCustonTemplate,this)
            mAdview = view.findViewById(R.id.nativead)
        } else {
            when(adtype){
                AD_TYPE_SMALL ->{
                    val view = inflate(context,R.layout.ad_small_default,this)
                    mAdview = view.findViewById(R.id.nativead)
                }
                AD_TYPE_MEDIUM ->{
                    val view = inflate(context,R.layout.ad_medium_default,this)
                    mAdview = view.findViewById(R.id.nativead)
                }
            }
        }

        mAppIcon = mAdview?.findViewById(R.id.ad_app_icon)
        mAppIcon?.visibility = GONE

        mLogo = mAdview?.findViewById(R.id.ad_logo)
        mLogo?.visibility = GONE

        mHeadline = mAdview?.findViewById(R.id.ad_headline)
        mHeadline?.visibility = GONE

        mAdBodyText = mAdview?.findViewById(R.id.ad_body)

        mStarRating = mAdview?.findViewById(R.id.ad_rating)
        mStarRating?.visibility = GONE

        mAdvertiser = mAdview?.findViewById(R.id.ad_advertiser)
        mAdvertiser?.visibility = GONE

        mPrice = mAdview?.findViewById(R.id.ad_price)
        mPrice?.visibility = GONE

        mStore = mAdview?.findViewById(R.id.ad_store)
        mStore?.visibility = GONE

        mMedia = mAdview?.findViewById(R.id.ad_media)
        mMedia?.visibility = GONE

        mImage = mAdview?.findViewById(R.id.ad_image)
        mImage?.visibility = GONE

        mActionButton = mAdview?.findViewById(R.id.ad_call_to_action)
        mBadge = mAdview?.findViewById(R.id.txt_ad_badge)
        setupAttributes(context,attrs)
    }

    fun setupAttributes(context: Context,attrs: AttributeSet){

        val badge_background = ta.getDrawable(
            R.styleable.AdmobView_av_backgroundBadge
        )


        if(badge_background != null){
            mBadge?.background = badge_background
        } else {
            mBadge?.background = ContextCompat.getDrawable(
                context,R.drawable.bg_adbadge
            )
        }

        val body_background = ta.getDrawable(
            R.styleable.AdmobView_av_backgroundBody
        )
        val bg_body = mAdview?.findViewById<LinearLayout>(R.id.container_body)
        if(body_background != null){
            bg_body?.background = body_background
        } else {
            bg_body?.background = ContextCompat.getDrawable(
                context,R.drawable.bg_adbody
            )
        }



        headlineAttributes()
        bodyAttributes()
        starRatingAttributes()
        ta.recycle()
    }
    fun headlineAttributes(){
        val headlincolor_default = ContextCompat.getColor(mContext,R.color.headline)
        val headlinecolor = ta.getColor(
            R.styleable.AdmobView_av_HeadlineColor,0
        )
        if(headlinecolor != 0) {
            mHeadline?.setTextColor(headlinecolor)
        } else {
            mHeadline?.setTextColor(headlincolor_default)
        }
        val dimen_headline_default = mContext.resources.getDimension(R.dimen.headlinetext)
        val headlinetextsize = ta.getDimensionPixelSize(R.styleable.AdmobView_av_HeadlineTextSize,
            0)
        if(headlinetextsize > 0){
            mHeadline?.setTextSize(TypedValue.COMPLEX_UNIT_PX, headlinetextsize.toFloat())
        } else {
            mHeadline?.setTextSize(TypedValue.COMPLEX_UNIT_PX, dimen_headline_default)
        }
        val fontfamily = ta.getResourceId(R.styleable.AdmobView_av_HeadlineFontFamily,0)
        if(fontfamily > 0){
            mHeadline?.setTypeface(
                ResourcesCompat.getFont(mContext,fontfamily)
            )
        }
    }
    fun bodyAttributes(){
        val bodycolor_default = ContextCompat.getColor(mContext,R.color.body)
        val bodycolor = ta.getColor(
            R.styleable.AdmobView_av_BodyTextColor,0
        )
        if(bodycolor != 0) {
            mAdBodyText?.setTextColor(bodycolor)
        } else {
            mAdBodyText?.setTextColor(bodycolor_default)
        }
        val dimen_body_default = mContext.resources.getDimension(R.dimen.body)
        val bodytextsize = ta.getDimensionPixelSize(R.styleable.AdmobView_av_BodyTextSize,
            0)
        if(bodytextsize > 0){
            mAdBodyText?.setTextSize(TypedValue.COMPLEX_UNIT_PX, bodytextsize.toFloat())
        } else {
            mAdBodyText?.setTextSize(TypedValue.COMPLEX_UNIT_PX, dimen_body_default)
        }
        val fontfamily = ta.getResourceId(R.styleable.AdmobView_av_BodyTextFontFamily,0)
        if(fontfamily > 0){
            mAdBodyText?.setTypeface(
                ResourcesCompat.getFont(mContext,fontfamily)
            )
        }
    }
    fun starRatingAttributes(){
        val starrating_default = ContextCompat.getColor(mContext,R.color.starrating)
        val starratingcolor = ta.getColor(
            R.styleable.AdmobView_av_StarRatingColor,0
        )
        if(starratingcolor != 0) {
           val drawable = mStarRating?.progressDrawable
            drawable?.let {
                DrawableCompat.setTint(it,starratingcolor)
            }
        } else {
            val drawable = mStarRating?.progressDrawable
            drawable?.let {
                DrawableCompat.setTint(it,starrating_default)
            }
        }
    }
    fun loadAd(context: Context,
               adid:String,
               listener: ModernAdmobListener?){
        mListener = listener
        adLoader = AdLoader.Builder(context,adid)
            .forNativeAd { nativead ->
                initAd(nativead)
            }
            .withAdListener(object : AdListener() {
                override fun onAdClicked() {
                    super.onAdClicked()
                    mListener?.onAdClicked()
                }

                override fun onAdClosed() {
                    super.onAdClosed()
                    mListener?.onAdClosed()
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    super.onAdFailedToLoad(error)
                    mListener?.onAdFailedToLoad(error)
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    mListener?.onAdImpression()
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    mListener?.onAdLoaded()
                }

                override fun onAdOpened() {
                    super.onAdOpened()
                    mListener?.onAdOpened()
                }
            })

            .build()
        adLoader?.loadAd(AdRequest.Builder().build())
    }

    fun initAd(nativeAd: NativeAd){
        nativeAd.icon?.let { icon ->
            mAppIcon?.visibility = View.VISIBLE
            mAppIcon?.setImageDrawable(icon.drawable)
            mAdview?.iconView = mAppIcon
        }

        nativeAd.headline?.let { hl ->
            mHeadline?.visibility = VISIBLE
            mHeadline?.text = hl
            mAdview?.headlineView = mHeadline
        }

        mAdBodyText?.text = nativeAd.body
        mAdview?.bodyView = mAdBodyText

        nativeAd.starRating?.let {
            mStarRating?.visibility = VISIBLE
            mStarRating?.rating = it.toFloat()
            mAdview?.starRatingView = mStarRating
        }


        if(nativeAd.advertiser != null){
            mAdvertiser?.visibility = VISIBLE
            mAdvertiser?.text = nativeAd.advertiser
            mAdview?.advertiserView = mAdvertiser
        }

        if(nativeAd.price != null){
            mPrice?.visibility = VISIBLE
            mPrice?.text = nativeAd.price
            mAdview?.priceView = mPrice
        }

        if(nativeAd.store != null){
            mStore?.visibility = VISIBLE
            mStore?.text = nativeAd.store
            mAdview?.storeView = mStore
        }

        val duration = nativeAd.mediaContent?.duration
        if(duration != null && duration > 0.0){
            mAdview?.mediaView = mMedia
            mMedia?.visibility = View.VISIBLE
        } else {
            if(nativeAd.images.isNotEmpty()){
                val img = nativeAd.images[0]
                mImage?.setImageDrawable(img.drawable)
                mImage?.visibility = View.VISIBLE
            }
        }


        mActionButton?.text = nativeAd.callToAction
        mAdview?.callToActionView = mActionButton

    }

    companion object {
        val COLOR_PRIMARY = R.color.colorprimary
        val COLOR_PRIMARY_DARK = R.color.colorprimarydark
        val COLOR_PRIMARY_LIGHT = R.color.colorprimarylight
        val COLOR_TEXT_ONPRIMARY = R.color.colortextonprimary
        val COLOR_TEXT_ONPRIMARYDARK = R.color.colortextonprimarydark
        val COLOR_TEXT_ONPRIMARYLIGHT = R.color.colortextonprimarylight
        val AD_TYPE_SMALL = 1
        val AD_TYPE_MEDIUM = 2

        val HEAD_TEXTSIZE = R.dimen.headlinetext
    }
    interface ModernAdmobListener{
        fun onAdClicked()
        fun onAdClosed()
        fun onAdFailedToLoad(error: LoadAdError)
        fun onAdImpression()
        fun onAdLoaded()
        fun onAdOpened()
    }
}