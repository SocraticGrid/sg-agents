/***********************************************************************************************************************
 *
 * Copyright (C) 2012 by Cognitive Medical Systems, Inc (http://www.cognitivemedciine.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 **********************************************************************************************************************/
 
 /***********************************************************************************************************************
 * Socratic Grid contains components to which third party terms apply. To comply with these terms, the following notice is provided:
 *
 * TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION
 * Copyright (c) 2008, Nationwide Health Information Network (NHIN) Connect. All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * - Neither the name of the NHIN Connect Project nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * END OF TERMS AND CONDITIONS
 *
 **********************************************************************************************************************/
if(!APPLICATION){
    Application = Clazz.extend({
        // Defines the global dom event listener 
	DOMEventListeners: new Hash(),
        _eventsQueue: [],
        _currentPackage: undefined,
        _currentWorkingSet: undefined,
        _currentUsername: undefined,
        
        construct: function(){
            this._registerOnEvent(EVENTS.EVENT_USER_CREDENTIALS_SET, 
                function(event){
                    this._currentUsername = event.username;
                }.bind(this)
            );
                
            this._registerOnEvent(EVENTS.EVENT_MAIN_PACKAGE_CHANGED, 
                function(event){
                    this._currentPackage = event.packageId;
                }.bind(this)
            );
                
            this._registerOnEvent(EVENTS.EVENT_MAIN_WORKING_SET_CHANGED, 
                function(event){
                    this._currentWorkingSet = event.workingSetId;
                }.bind(this)
            );
        },
        
        _registerOnEvent: function(eventType, callback){
            if(!(this.DOMEventListeners.keys().member(eventType))) {
                    this.DOMEventListeners[eventType] = [];
            }

            this.DOMEventListeners[eventType].push(callback);
        },
        _unregisterOnEvent: function(eventType, callback){
            if(this.DOMEventListeners.keys().member(eventType)) {
                this.DOMEventListeners[eventType] = this.DOMEventListeners[eventType].without(callback);
            } else {
                // Event is not supported
                // TODO: Error Handling
            }
        },
        _handleEvents: function(event, payload) {
		
		/* Force execution if necessary. Used while handle Layout-Callbacks. */
		if(event.forceExecution) {
			this._executeEventImmediately({event: event, arg: payload});
		} else {
			this._eventsQueue.push({event: event, arg: payload});
		}
		
		if(!this._queueRunning) {
			this._executeEvents();
		}
		
		// TODO: Make this return whether no listener returned false.
		// So that, when one considers bubbling undesireable, it won't happen.
		return false;
	},
        /**
	* Helper method to execute an event immediately. The event is not
	* scheduled in the _eventsQueue. Needed to handle Layout-Callbacks.
	*/
	_executeEventImmediately: function(eventObj) {
		if(this.DOMEventListeners.keys().member(eventObj.event.type)) {
			this.DOMEventListeners[eventObj.event.type].each((function(value) {
				value(eventObj.event, eventObj.arg);		
			}).bind(this));
		}
	},

	_executeEvents: function() {
		this._queueRunning = true;
		while(this._eventsQueue.length > 0) {
			var val = this._eventsQueue.shift();
                        try{
                            this._executeEventImmediately(val);
                        }catch(ex){
                            console.log(ex.message);
                            console.log(ex);
                        }
			
		}
		this._queueRunning = false;
	},
        
        _getCurrentPackage: function(){
            return this._currentPackage;
        },
        
        _getCurrentWorkingSet: function(){
            return this._currentWorkingSet;
        },
        
        _getCurrentUsername: function(){
            return this._currentUsername;
        }
        
    });
    
    var APPLICATION = new Application();
} 

if(!APPLICATION.EVENTS){
    
    /**
     * Facade for APPLICATION Event handling methods
     */
    APPLICATION.EVENTS = {
        registerOnEvent:		APPLICATION._registerOnEvent.bind(APPLICATION),
        unregisterOnEvent:		APPLICATION._unregisterOnEvent.bind(APPLICATION),
        raiseEvent:			APPLICATION._handleEvents.bind(APPLICATION)
    }
    
} 

if(!APPLICATION.CONTEXT_INFO){
    /**
     * Facade for Common used information
     */
    APPLICATION.CONTEXT_INFO = {
        getCurrentPackage:		APPLICATION._getCurrentPackage.bind(APPLICATION),
        getCurrentWorkingSet:		APPLICATION._getCurrentWorkingSet.bind(APPLICATION),
        getCurrentUsername:		APPLICATION._getCurrentUsername.bind(APPLICATION)
    }
}
