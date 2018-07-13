/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.droidteahouse.cooperhewitt.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.paging.LivePagedListBuilder
import android.support.annotation.MainThread
import android.util.Log
import com.droidteahouse.cooperhewitt.db.ArtDb
import com.droidteahouse.cooperhewitt.vo.ArtObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository implementation that uses a database PagedList + a boundary callback to return a
 * listing that loads in pages.
 *      // create a boundary callback which will observe when the user reaches to the edges of
// the list and update the database with extra data.


// we are using a mutable live data to trigger refresh requests which eventually calls
// refresh method and gets a new live data. Each refresh request by the user becomes a newly
// dispatched data in refreshTrigger

 */
@Singleton
class ArtObjectRepository @Inject constructor(
    var boundaryCallback: ArtBoundaryCallback,
    var db: ArtDb) {

  var listing: Listing<ArtObject>

  init {
    val builder: LivePagedListBuilder<Int, ArtObject> by lazy {
      LivePagedListBuilder(db.artDao().artObjects(), 10)
          .setBoundaryCallback(boundaryCallback)
    }
    val refreshTrigger = MutableLiveData<Unit>()
    val refreshState = Transformations.switchMap(refreshTrigger, {
      //@todo fixo
      refresh(1)
    })
    listing =
        Listing(
            pagedList = builder.build(),
            networkState = boundaryCallback.networkState,
            retry = {
              boundaryCallback.helper.retryAllFailed()
            },
            refresh = {
              refreshTrigger.value = null
            },
            refreshState = refreshState
        )

    boundaryCallback.handleResponse = this::insertResultIntoDb


  }

  /**
   * Inserts the response into the database while also assigning position indices to items.
   */
  fun insertResultIntoDb(list: List<ArtObject>) {
    list.let { results ->
      //
      db.runInTransaction {
        val nextPage = db.artDao().getNextPageInArt()
        val items = results.map { item ->
          //load bitmap here expensive, make map and go back to delete?
          //
          item.page = nextPage
          item.imageUrl = item.images?.get(0)?.n?.url
          item.hash = item.id.toInt()  //init before hash
          item
        }
        Log.d("REPO", "items insert starting" + items.size)
        //@todo when insert it calls onchange--need to protect from extra calls to network?
        //Paging data source is getting next page?  check page
        db.artDao().insert(items)
      }
    }
  }


  /**
   * When refresh is called, we simply run a fresh network request and when it arrives, clear
   * the database table and insert all new items in a transaction.
   * <p>
   * Since the PagedList already uses a database bound data source, it will automatically be
   * updated after the database transaction is finished.
   */
  //@todo not needed until search impl
  @MainThread
  private fun refresh(page: Int): LiveData<NetworkState> {

    val networkState = MutableLiveData<NetworkState>()
    networkState.value = NetworkState.LOADING
    /*
    artAPI.japaneseDesign(page).enqueue(
            object : Callback<ArtObjects> {
                override fun onFailure(call: Call<ArtObjects>, t: Throwable) {
                    // retrofit calls this on main thread so safe to call set value
                    networkState.value = NetworkState.error(t.message)
                }

                override fun onResponse(
                        call: Call<ArtObjects>,
                        response: Response<ArtObjects>) {
                    ioExecutor.execute {
                        db.runInTransaction {
                            //db.artDao().deleteByPage(page)
                            insertResultIntoDb(response.body())
                        }
                        // since we are in bg thread now, post the result.
                        networkState.postValue(NetworkState.LOADED)
                    }
                }
            }
    )*/
    networkState.postValue(NetworkState.LOADED)
    return networkState
  }

  /**
   * Returns japanese design objects.
   */
  @MainThread
  fun getArtObjects(): Listing<ArtObject> {

    //@todo inject or cache
    return listing
  }

  fun update(item: ArtObject) {
    db.artDao().update(item)

  }

  fun delete(item: ArtObject) {
    db.artDao().delete(item)
  }
}

