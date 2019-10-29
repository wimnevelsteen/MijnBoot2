package com.example.mijnboot.communications;

import java.util.ArrayList;

public class Directions {
    public byte[] toBluetoothStream(int direction, int speed) {
        ArrayList<Byte> output = new ArrayList<>();
        output.add(Protocol.COMMAND_DIRECTION.getBytes()[0]);
        for(byte elem: String.valueOf(direction).getBytes()) {
            output.add(elem);
        }
        output.add(Protocol.COMMAND_SPEED.getBytes()[0]);
        for(byte elem: String.valueOf(speed).getBytes()) {
            output.add(elem);
        }
        output.add(Protocol.COMMAND_TERMINATOR.getBytes()[0]);

        byte[] outputArray =  new byte[output.size()];
        for (int i=0; i < output.size();i++) {
            outputArray[i] = output.get(i);
        }

        return outputArray;
    }
}
