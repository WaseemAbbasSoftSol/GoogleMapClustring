package com.example.mapclustring.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.mapclustring.R
import com.example.mapclustring.model.Dealer
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator


class DealerRender(
    private val context: Context,
    map: GoogleMap,
    clusterManager: ClusterManager<Dealer>
) :
    DefaultClusterRenderer<Dealer>(context, map, clusterManager) {

    private val mIconGenerator = IconGenerator(context)
    private val mClusterIconGenerator = IconGenerator(context)
    private val mClusterImageView: ImageView
    private val mImageView: ImageView

    private val mImageDimension: Int

    init {
        val multiProfile = LayoutInflater.from(context).inflate(
            R.layout.layout_cluster_marker,
            null
        )
        mClusterIconGenerator.setContentView(multiProfile)
        mClusterImageView = multiProfile.findViewById(R.id.imagemap)

        mImageDimension = context.resources.getDimension(R.dimen.profile_image_size).toInt()
        mImageView = ImageView(context)
        mImageView.layoutParams = ViewGroup.LayoutParams(mImageDimension, mImageDimension)
        mIconGenerator.setContentView(mImageView)
    }

    override fun onBeforeClusterItemRendered(item: Dealer, markerOptions: MarkerOptions) {
        Glide.with(context)
            .asBitmap()
            .load(item.profilePhoto)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .fitCenter()
            .placeholder(R.mipmap.ic_launcher).dontAnimate().into(mImageView)

        val icon = mIconGenerator.makeIcon()
        markerOptions
            .icon(BitmapDescriptorFactory.fromBitmap(icon))
            .title(item.name)
    }

    override fun onBeforeClusterRendered(cluster: Cluster<Dealer>, markerOptions: MarkerOptions) {
        Glide.with(context)
            .asBitmap()
            .load(cluster.items.elementAt(0).profilePhoto)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .fitCenter()
            .placeholder(R.mipmap.ic_launcher).dontAnimate().into(mClusterImageView)

//        val profilePhotos: MutableList<Drawable> = ArrayList(4.coerceAtMost(cluster.size))
//        val width: Int = mImageDimension
//        val height: Int = mImageDimension
//        var dummyBitmap: Bitmap? = null
//        var drawable: Drawable
//
//        for (dealer in cluster.items){
//            if (profilePhotos.size == 4) break
//            try {
//                dummyBitmap = Glide.with(context).asBitmap().load(dealer.profilePhoto)
//                    .submit(70,70).get()
//            } catch (e: Exception) {
//                Log.d("log_d_m", e.message.toString())
//            }
//            drawable = BitmapDrawable(context.resources, dummyBitmap)
//            drawable.setBounds(0, 0, width, height)
//            profilePhotos.add(drawable)
//        }
//        val multiDrawable = MultiDrawable(profilePhotos)
//        multiDrawable.setBounds(0, 0, width, height)
//        mClusterImageView.setImageDrawable(multiDrawable)
        val icon = mClusterIconGenerator.makeIcon(cluster.size.toString())
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon))
    }

    override fun shouldRenderAsCluster(cluster: Cluster<Dealer>): Boolean = cluster.size > 1
}