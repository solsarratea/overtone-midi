# overtone-midi

Resources to handle midi files when livecoding with Overtone.

## Dependencies

  [Overtone](https://github.com/overtone/overtone)


  It is recommended to use [emacs-live](https://github.com/overtone/emacs-live)

## Usage

### Init

  Run ```lein repl```
  Once session is started, run ```(-load)``` for loading namespaces.


### Dev
  You may use clojure.tools.namespace.repl.

  Run ```refresh``` for updating namespaces.


### MIDI file -> piano

  Parse midi file to be played by overtone piano.
#### Example:
  Run comment lines in ``` overtone-midi.piano-midi ```

  Or either run in REPL:

     (in-ns 'overtone-midi.piano-midi)  

     (-> "love-dream" parser/read-and-parse-midi play-on-piano)`

If you are a piano player you can record yourself using [piano->MIDI](https://piano-scribe.glitch.me/).

Add it to ``` /resources/midis ```

### MIDI file -> code
 
Use REPL to select interval of MIDI-file to parse, and transcribe it into a namespace.

Run
  ```(midi2code)```

Set configuration: 
  - select MIDI file from resources 
  - bounds of interval
  - tempo (scale factor for duration of events).

You may play sample, and edit it until you get what you need.

Once you end up with editing, use created function to play sample.
 
<a href="https://midilosaurus.glitch.me?wvideo=6hu3nk5js0"><img src="https://embedwistia-a.akamaihd.net/deliveries/761cab5c843dcac3a8a254150db39311.jpg?wistia-6hu3nk5js0-1-6hu3nk5js0-video-thumbnail=1&amp;image_play_button_size=2x&amp;image_crop_resized=960x540&amp;image_play_button=1&amp;image_play_button_color=cf00e4e0" width="400" height="225" style="width: 400px; height: 225px;"></a>


**Use it for livecoding :purple_heart:**



## License

Copyright © 2019 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
