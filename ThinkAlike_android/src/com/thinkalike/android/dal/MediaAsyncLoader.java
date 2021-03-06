/**
* Copyright 2013-2014 Tiancheng Hu
* 
* Licensed under the GNU Lesser General Public License, version 3.0 (LGPL-3.0, the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*     http://opensource.org/licenses/lgpl-3.0.html
*     
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.thinkalike.android.dal;

import java.util.ArrayList;

import com.thinkalike.android.dal.AsyncResourceTask.WorkType;

//IMPROVE: build up Async mechanism in generic module(ref source of android.os.AsyncTask), and remove platform-dependent ones.
public class MediaAsyncLoader {
	//-- Constants and Enums -----------------------------------
	//IMPROVE: check if it's necessary to maintain a AsyncTask pool by ourselves. 
	//NOTE: if the following amount is too small, amount of simultaneous AsyncTasks will be constrained, and oldest ones will be CANCELLED. 
	private final static int COUNT_ASYNCWORKERS_MAX = 50;
	
	//-- Inner Classes and Structures --------------------------
	public interface OnMediaLoadListener {
		public void onMediaLoaded(Object media, Object tag);
		public void onError(int errCode);
	}
	
	//-- Delegates and Events ----------------------------------
	//-- Instance and Shared Fields ----------------------------
	private static ArrayList<AsyncResourceTask> _asyncWorkers = new ArrayList<AsyncResourceTask>();
	
	//-- Properties --------------------------------------------
	//-- Constructors ------------------------------------------
	//-- Destructors -------------------------------------------
	//-- Base Class Overrides ----------------------------------
	//-- Public and internal Methods ---------------------------
	public static boolean asyncLoadImageFile(String loadPath, int width_limit, int height_limit, OnMediaLoadListener mediaLoadListener) {
		AsyncResourceTask asyncWorker = requestNewAsyncWorker(WorkType.Image, mediaLoadListener);
		asyncWorker.execute(loadPath, width_limit, height_limit);
		return true;
	}
	
	//-- Private and Protected Methods -------------------------
	private static AsyncResourceTask requestNewAsyncWorker(WorkType workType, OnMediaLoadListener mediaLoadListener) {
		AsyncResourceTask asyncWorker = new AsyncResourceTask(workType, mediaLoadListener);
		if(asyncWorker!=null){
			//IMPROVE: 1.prepare a "fast-food service" list (like below) and a blockable list 2.provide cancel() I/F through asyncLoadImageFile()
			if(_asyncWorkers.size() >= COUNT_ASYNCWORKERS_MAX){
				//cancel and remove the first AsyncTask (FIFO)
				_asyncWorkers.get(0).cancel(true);
				_asyncWorkers.remove(0);
			}
			_asyncWorkers.add(asyncWorker);
		}
		return asyncWorker;
	}

	//-- Event Handlers ----------------------------------------
}
