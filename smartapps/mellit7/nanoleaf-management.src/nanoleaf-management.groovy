/**
 *
 *  Nanoleaf Setup Info and Control
 *
 *  Version 1.0 December 3, 2019
 *
 *  Author: Melinda Little 2019
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *

 */

definition(
    name: "Nanoleaf Management",
    namespace: "mellit7",
    author: "Melinda Little",
    description: "Manage Nanoleaf set up and use ",
    category: "Convenience",
    iconUrl: "http://stevethegeek.net/smartthings/aurora/aurora-on.png",
    iconX2Url: "http://stevethegeek.net/smartthings/aurora/aurora-on.png"
)

preferences {

    page(name: "displayDetails", title: "Nanoleaf Management")
    page(name: "pickScene", title: "Select a Scene")   
    page(name: "getAPI", title: "Get API Key")
    page(name: "cleanAPI", title: "Clear API Key")
    page(name: "setScene", title: "Scene Set")    
    page(name: "newLeaf", title: "Nanoleaf Selection")
    page(name: "leafStatus", title: "Nanoleaf Information")
    page(name: "idPanels", title: "Panel Identification")
}

def installed()
{
	log.debug "INSTALLED"
 
}

def updated()
{
	log.debug "UPDATED"

}

def displayDetails() {

	if (!theNanoleaf) {
		return dynamicPage(name: "displayDetails", title: "Nanoleaf Selection", uninstall: true) {
			section("Select your Nanoleaf") {
				input "theNanoleaf", "device.NanoleafAuroraSmarterAPI", multiple: false, required: true, title: "Nanoleaf?", submitOnChange: true
			}
		}
	}
	else {

    	def presetsMap = null

		if (theNanoleaf.currentValue("presets")?.trim()) {
    		presetsMap = new groovy.json.JsonSlurper().parseText(theNanoleaf.currentValue("presets"))
    	}
    
   		return dynamicPage(name: "displayDetails", uninstall: true, install: true) {
    
         	section ("${theNanoleaf.name} Actions"){
         	  	if (presetsMap.size() >0) {
		      		href(name: "sceneSelect",title: "Activate a Scene",required: false,page: "pickScene", description: "Tap to select a scene")
              	}
              	
                href(name: "panels",title: "Identify Panels",required: false,page: "idPanels", description: "Tap to identify panels")
                href(name: "leafStats",title: "View ${theNanoleaf.name} Information ",required: false,page: "leafStatus", description: "Tap to view ${theNanoleaf.name} information")
                href(name: "APIset",title: "Get an API Key",required: false,page: "getAPI", description: "Tap to get an API Key")
                href(name: "APIclear",title: "Clear the API Key",required: false,page: "clearAPI", description: "Tap to clear the API Key")
            	href(name: "leafChange",title: "Select a Different Nanoleaf",required: false,page: "newLeaf", description: "Tap to change Nanoleafs")                
         	}    
   	     	
     	}
    } 
}

def pickScene() {

    def curPresets = theNanoleaf.currentValue("presets")
    def presetsMap = new groovy.json.JsonSlurper().parseText(curPresets)
//    log.debug presetsMap.name

   	return dynamicPage(name: "pickScene", nextPage: "setScene") {
         section ("${theNanoleaf.name} Scenes"){
              input name: "selectedScene", type: "enum", options: presetsMap.name, description: "Select the Scene to Activate", 
    			defaultValue: "" , required: no
         } 

    }

}

def setScene() {

	log.debug "Setting A Scene ${selectedScene}"
    theNanoleaf.changeScene(selectedScene)
   	return dynamicPage(name: "setScene", nextPage: "displayDetails") {
         section ("${theNanoleaf.name} Scene \"${selectedScene}\" Set"){

         } 

    }

}

def getAPI() {

    theNanoleaf.requestAPIkey()
   	return dynamicPage(name: "getAPI", nextPage: "displayDetails") {
         section ("An API Key for then ${theNanoleaf.name} has been requested"){

         } 

    }


}

def clearAPI() {

    theNanoleaf.clearApiKey()
   	return dynamicPage(name: "clearAPI", nextPage: "displayDetails") {
         section ("The API Key for the ${theNanoleaf.name} has been removed"){

         } 

    }


}

def newLeaf() {

		return dynamicPage(name: "newLeaf", title: "Nanoleaf Selection", nextPage: "displayDetails") {
			section("Select your Nanoleaf") {
				input "theNanoleaf", "device.NanoleafAuroraSmarterAPI", multiple: false, required: true, title: "Nanoleaf?"
			}
		}

}

def leafStatus() {
		def scene1 = theNanoleaf.currentValue("scene1")
		def scene2 = theNanoleaf.currentValue("scene2")
		def scene3 = theNanoleaf.currentValue("scene3")
		def curScene = theNanoleaf.currentValue("scene")
   		def curDeviceInfo = theNanoleaf.currentValue("IPinfo")
   		def curAPI = theNanoleaf.currentValue("retrievedAPIkey")
    	def curAPIStatus = theNanoleaf.currentValue("apiKeyStatus")
        
		return dynamicPage(name: "leafStatus", title: "Nanoleaf Information", nextPage: "displayDetails") {
			section ("${theNanoleaf.name} Status Information") {
           	  	paragraph "Device Info:  ${curDeviceInfo}\nRetrieved API Key:  ${curAPI}\nAPI Status:  ${curAPIStatus}" 
              	paragraph "Curent scene: ${curScene}"  
              	paragraph "Scene 1: ${scene1}\nScene 2: ${scene2}\nScene 3: ${scene3}" 

         	}
		}

}

def idPanels() {

		def panels = theNanoleaf.currentValue("panelIds").split(",")
        def colors = ["Red","Blue","Green","Yellow","Orange","Pink","Purple","Black","White"]
//        log.debug panels.size()
        def setColor
        def colorIndex = 0
        def setNumber = 0
        def pageText = "${theNanoleaf.name} panels:"
        for (int i = 0; i < panels.size();i++) {
        	if (colorIndex == 8) {setNumber++}
        	colorIndex = i - (9*setNumber)	
      		setColor = colors[colorIndex]
//            log.debug colorIndex 
            pageText = "${pageText}\nPanel Id ${panels[i]} is ${colors[colorIndex]}"
            theNanoleaf.setPanelColor(panels[i], colors[colorIndex], false)
        }

		return dynamicPage(name: "idPanels", title: "${theNanoleaf.name} Panel Identification", nextPage: "displayDetails") {
			section ("${theNanoleaf.name} Panels") {
           	  	paragraph pageText

         	}
		}
}