package jp.techacademy.yuya.tajima.apiapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class ApiAdapter(private val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    // 取得したJsonデータを解析し、Shop型オブジェクトとして生成したものを格納するリスト
    private val items = mutableListOf<Shop>()

    // 一覧画面から登録するときのコールバック（FavoriteFragmentへ通知するメソッド)
    var onClickAddFavorite: ((FavoriteInput) -> Unit)? = null
    // 一覧画面から削除するときのコールバック（ApiFragmentへ通知するメソッド)
    var onClickDeleteFavorite: ((Shop) -> Unit)? = null

    // Itemを押したときのメソッド
    var onClickItem: ((String, FavoriteInput) -> Unit)? = null

    // 表示リスト更新時に呼び出すメソッド
    fun refresh(list: List<Shop>) {
        update(list, false)
    }

    fun add(list: List<Shop>) {
        update(list, true)
    }

    // 表示リスト更新時に呼び出すメソッド
    fun update(list: List<Shop>, isAdd: Boolean) {
        items.apply {
            if(!isAdd){ // 追加のときは、Clearしない
                clear() // items を 空にする
            }
            addAll(list) // itemsにlistを全て追加する
        }
        notifyDataSetChanged() // recyclerViewを再描画させる
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            // ViewTypeがVIEW_TYPE_EMPTY（つまり、取得件数が0件）の場合
            VIEW_TYPE_EMPTY -> EmptyViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_list_empty, parent, false)
            )
            // 上記以外（つまり、1件以上の件数が取得された場合
            else -> ApiItemViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_list, parent, false))
        }
    }

    // ViewHolderを継承したApiItemViewHolderクラスの定義
    class ApiItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
        // レイアウトファイルからidがrootViewのConstraintLayoutオブジェクトを取得し、代入
        val rootView : ConstraintLayout = view.findViewById(R.id.rootView)
        // レイアウトファイルからidがnameTextViewのTextViewオブジェクトを取得し、代入
        val nameTextView: TextView = view.findViewById(R.id.nameTextView)
        // レイアウトファイルからidがimageViewのImageViewオブジェクトを取得し、代入
        val imageView: ImageView = view.findViewById(R.id.imageView)
        // レイアウトファイルからidがfavoriteImageViewのImageViewオブジェクトを取得し、代入
        val favoriteImageView: ImageView = view.findViewById(R.id.favoriteImageView)
        // レイアウトファイルからidがaddressTextViewのTextViewオブジェクトを取得し、代入
        val addressTextView: TextView = view.findViewById(R.id.addressTextView)
    }

    // 取得件数が0件の時
    class EmptyViewHolder(view: View): RecyclerView.ViewHolder(view)

    override fun getItemCount(): Int {
        // itemsプロパティに格納されている要素数を返す
        return if (items.isEmpty()) 1 else items.size
    }

    // onCreateViewHolderの第二引数はここで決める。条件によってViewTypeを返すようにすると、一つのRecyclerViewで様々なViewがある物が作れる
    override fun getItemViewType(position: Int): Int {
        return if (items.isEmpty()) VIEW_TYPE_EMPTY else VIEW_TYPE_ITEM
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ApiItemViewHolder) {
            // 生成されたViewHolderがApiItemViewHolderだったら。。。
            updateApiItemViewHolder(holder, position)
        }// {
        // 別のViewHolderをバインドさせることが可能となる
        // }
    }

    private fun updateApiItemViewHolder(holder: ApiItemViewHolder, position: Int) {
        // 生成されたViewHolderの位置を指定し、オブジェクトを代入
        val data = items[position]
        // お気に入り状態を取得
        val isFavorite = FavoriteShop.findBy(data.id) != null

        // 画像URL
        val url = if (data.couponUrls.sp.isNotEmpty()) data.couponUrls.sp else data.couponUrls.pc

        // お気入りデータ登録用データ作成
        val favoriteInput = FavoriteInput( id = data.id, imageUrl = data.logoImage, name = data.name, url = url )

        holder.apply {
            rootView.apply {
                // 偶数番目と奇数番目で背景色を変更させる
                setBackgroundColor(ContextCompat.getColor(context,
                    if (position % 2 == 0) android.R.color.white else android.R.color.darker_gray))
                setOnClickListener {
                    onClickItem?.invoke(url, favoriteInput)
                }
            }
            // nameTextViewのtextプロパティに代入されたオブジェクトのnameプロパティを代入
            nameTextView.text = data.name
            // addressTextViewのtextプロパティに代入されたオブジェクトのaddressプロパティを代入
            addressTextView.text = data.address

            // Picassoライブラリを使い、imageViewにdata.logoImageのurlの画像を読み込ませる
            Picasso.get().load(data.logoImage).into(imageView)
            // 白抜きの星マークの画像を指定
            favoriteImageView.apply {
                setImageResource(if (isFavorite) R.drawable.ic_star else R.drawable.ic_star_border) // Picassoというライブラリを使ってImageVIewに画像をはめ込む
                setOnClickListener {
                    if (isFavorite) {
                        onClickDeleteFavorite?.invoke(data)
                    } else {
                        onClickAddFavorite?.invoke(favoriteInput)
                    }
                    notifyItemChanged(position)
                }
            }
        }
    }

    companion object {
        // Viewの種類を表現する定数、こちらは1件以上取得できた時
        private const val VIEW_TYPE_ITEM = 0
        // Viewの種類を表現する定数、こちらは１件も取得できなかった時
        private const val VIEW_TYPE_EMPTY = 1
    }
}