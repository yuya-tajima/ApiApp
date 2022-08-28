package jp.techacademy.yuya.tajima.apiapp

interface FavoriteOperations {
    // お気に入り追加時の処理
    fun onAddFavorite(favoriteInput: FavoriteInput)
    // お気に入り削除時の処理
    fun onDeleteFavorite(id: String)
}