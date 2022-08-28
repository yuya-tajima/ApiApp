package jp.techacademy.yuya.tajima.apiapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_web_view.*
import kotlinx.android.synthetic.main.recycler_favorite.view.*

class WebViewActivity: AppCompatActivity(), FavoriteOperations {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        val favoriteInputJson = intent.getStringExtra(FAV_INPUT)
        val favoriteInput = Gson().fromJson<FavoriteInput>(favoriteInputJson, FavoriteInput::class.java)

        Log.d("PRINT_DEBUG", "serialized $favoriteInput")

        var isFavorite = FavoriteShop.findBy(favoriteInput.id) != null

        recycler_favorite_layout.nameTextView.text = favoriteInput.name
        Picasso.get().load(favoriteInput.imageUrl).into(recycler_favorite_layout.imageView)

        recycler_favorite_layout.favoriteImageView.apply {
            setImageResource(if (isFavorite) R.drawable.ic_star else R.drawable.ic_star_border)
            setOnClickListener {
                if (isFavorite) {
                    onDeleteFavorite(favoriteInput.id)
                    isFavorite = false
                } else {
                    onAddFavorite(favoriteInput)
                    setImageResource(R.drawable.ic_star)
                    isFavorite = true
                }
            }
        }

        webView.loadUrl(intent.getStringExtra(KEY_URL).toString())
    }

    companion object {
        private const val KEY_URL = "key_url"
        private const val FAV_INPUT = "fav_input"
        fun start(activity: Activity, url: String, favoriteInput: FavoriteInput) {
            val intent = Intent(activity, WebViewActivity::class.java)
            intent.putExtra(KEY_URL, url)
            intent.putExtra(FAV_INPUT, Gson().toJson(favoriteInput))
            activity.startActivity(intent)
        }
    }

    override fun onAddFavorite(favoriteInput: FavoriteInput) {
        FavoriteShop.insert(FavoriteShop().apply {
            id = favoriteInput.id
            name = favoriteInput.name
            imageUrl = favoriteInput.imageUrl
            url = favoriteInput.url
        })
    }

    override fun onDeleteFavorite(id: String) {
        showConfirmDeleteFavoriteDialog(this, id ) {
            deleteFavorite(it)
        }
    }

    private fun deleteFavorite(id: String) {
        FavoriteShop.delete(id)
        recycler_favorite_layout.favoriteImageView.setImageResource(R.drawable.ic_star_border)
    }
}
