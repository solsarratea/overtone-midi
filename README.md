# overtone-midi

Resources to handle midi files when livecoding with Overtone.

## Dependencies
  
  [Overtone](https://github.com/overtone/overtone)
  
  
  It is recommended to use [emacs-live](https://github.com/overtone/emacs-live)
  
## Usage


### MIDI file -> piano


  Parse midi file to be played by overtone piano.
  
  Example:
    
    load namespace in REPL
    
      `(in-ns 'overtone-midi.piano-midi)`
    
    run
    
      `(-> "love-dream" midi parse-midi play-on-piano)`
     
If you are a piano player you can record yourself using [piano->MIDI](https://piano-scribe.glitch.me/).

Add it to ``` /resources/midis ```


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
