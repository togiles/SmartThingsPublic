/**
 *  Turn Off Lights
 *
 *  Copyright 2014 Todd Giles
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
    name: "Turn Off Lights",
    namespace: "togiles",
    author: "Todd Giles",
    description: "Dim lights eventually turning off, if they are on.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
    section  ("Select dimmers to turn off..."){ 
        input "lights", "capability.switchLevel", multiple: true, required: true
	}
	section("At this time...") {
		input "offTime", "time", title: "When?"
	}
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	unschedule()
	initialize()
}

def initialize() {
	schedule(offTime, dimLightsUntilOff)
}

def dimLightsUntilOff() {
    log.debug "dimLightsUntilOff :: ${lights}"
	for (light in lights) {
        log.debug "light :: ${light}"
    	def switchVal = light.latestValue("switch")
        def levelVal = light.latestValue("level")
		if (switchVal == "on" && levelVal < 10) {
          log.debug "turning light off"
          light.off()
        } else if (switchVal == "on") {
          def newLevel = levelVal - 5
          log.debug "setting light level to ${newLevel}"
          light.setLevel(newLevel)
          runIn(30, "dimLightsUntilOff")
        }
    }
}
