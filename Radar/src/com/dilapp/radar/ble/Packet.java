package com.dilapp.radar.ble;

import java.util.ArrayList;
import java.util.Arrays;

public class Packet {

	public static final int PacketSize_MAX = 20;
	public static final int Length_Of_Packet_Header = 4;
	public static final int Length_Of_Packet_Header_OPT = 1;
	public static final int Length_Of_SplitPacketBody = 15;

	public static ArrayList<byte[]> splitBody(byte[] orignal) throws Exception {
		ArrayList<byte[]> splitBody = new ArrayList<byte[]>();
		int packetlength = orignal.length;
		if (packetlength < Length_Of_Packet_Header)
			throw new Exception("Unsupported buffer");
		if (orignal.length <= PacketSize_MAX) {
			// orignal[3] = (byte) (orignal[3] & 0xFE);
			splitBody.add(orignal);
			return splitBody;
		}
		// TODO
		int piecenum = (int) (packetlength - Length_Of_Packet_Header)
				/ Length_Of_SplitPacketBody
				+ (packetlength - Length_Of_Packet_Header)
				% Length_Of_SplitPacketBody == 0 ? 0 : 1;
		byte[] header = new byte[4];
		header[0] = orignal[0];
		header[1] = orignal[1];
		header[2] = orignal[2];
		// header[3] = (byte) (orignal[3] & 0xFC);// add flag to mark this
		// packet
		// // is split into some pieces
		header[3] = (byte) (orignal[3]/* | 0x01 */);

		int offset = Length_Of_Packet_Header;

		for (int index = 0; index < piecenum; index++) {
			offset = Length_Of_Packet_Header + Length_Of_SplitPacketBody
					* index;
			int endoffset = index == (piecenum - 1) ? packetlength - 1 : offset
					+ Length_Of_SplitPacketBody;
			byte[] splitpiece = Arrays.copyOfRange(orignal, offset, endoffset);
			byte[] opt = new byte[1];
			opt[0] = (byte) ((piecenum & 0x0F) | ((index & 0xF) << 4));
			byte[] newpacket = new byte[20];
			int tmpoff = 0;
			System.arraycopy(header, 0, newpacket, tmpoff, header.length);
			tmpoff += header.length;
			System.arraycopy(opt, 0, newpacket, tmpoff, opt.length);
			tmpoff += opt.length;
			System.arraycopy(splitpiece, 0, newpacket, tmpoff,splitpiece.length);
			splitBody.add(newpacket);
		}

		return splitBody;

	}

}
