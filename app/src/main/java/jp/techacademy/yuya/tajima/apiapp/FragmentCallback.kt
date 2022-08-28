package jp.techacademy.yuya.tajima.apiapp

interface FragmentCallback: FavoriteOperations {
    // Itemを押したときの処理
    fun onClickItem(url: String, favoriteInput: FavoriteInput)
}