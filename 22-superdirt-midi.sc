
**********MIDI OUT**********

MIDIClient.init; // this line initialises the SuperCollider midi client

//You should see a list of available midi devices in the post window

// substitute your own device here

~midiOut = MIDIOut.newByName("LoopBe Internal MIDI", "LoopBe Internal MIDI");

//if you want to connect more devices you need to repear the above line of code, but replace `~midiOut` with a different name

//e.g.

~midiOut1 = MIDIOut.newByNAme("your other device", "your other device");

 // then boot superdirt

SuperDirt.start


//Next we need to let SuperDirt know about the devices we've added

~dirt.soundLibrary.addMIDI(\qc, ~midiOut); // you can replace `midi1` with whatever friendly name you like - this will be the name you use in the mini-notation back in tidal

//if you set up an additional midi device don't forget to add it here too:

~dirt.soundLibrary.addMIDI(\midi2, ~midiOut1);


//and you can adjust the latency here:

~midiOut.latency = 0.45;

~midiOut1.latency = 0.1;


**********MIDI IN**********

//To accept MIDI in to Tidal we need to run a bit more code - this lets SuperCollider convert MIDI inputs from your controller into OSC messages

// Evaluate the block below to start the mapping MIDI -> OSC.

(
var on, off, cc;
var osc;

osc = NetAddr.new("127.0.0.1", 6010);

MIDIClient.init;
MIDIIn.connectAll;

on = MIDIFunc.noteOn({ |val, num, chan, src|
	osc.sendMsg("/ctrl", num.asString, val/127);
});

off = MIDIFunc.noteOff({ |val, num, chan, src|
	osc.sendMsg("/ctrl", num.asString, 0);
});

cc = MIDIFunc.cc({ |val, num, chan, src|
	osc.sendMsg("/ctrl", num.asString, val/127);
});

if (~stopMidiToOsc != nil, {
	~stopMidiToOsc.value;
});

~stopMidiToOsc = {
	on.free;
	off.free;
	cc.free;
};
)

// Evaluate the line below to stop it.
~stopMidiToOsc.value;
