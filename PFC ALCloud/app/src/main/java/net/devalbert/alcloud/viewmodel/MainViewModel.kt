package net.azarquiel.caravanretrofit.viewmodel

import android.provider.Settings
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.devalbert.firebasestorage.api.MainRepository
import net.devalbert.firebasestorage.model.Items

// ……

/**
 * Created by pacopulido on 23/02/2021.
 */
class MainViewModel : ViewModel() {

    private var repository: MainRepository = MainRepository()

    fun getArchivos(): MutableLiveData<List<Items>> {
        val games = MutableLiveData<List<Items>>()
        GlobalScope.launch(Main) {
            games.value = repository.getArchivos()
        }
        return games
    }
}
