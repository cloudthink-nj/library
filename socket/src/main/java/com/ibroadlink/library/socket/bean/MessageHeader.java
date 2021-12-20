package com.ibroadlink.library.socket.bean;


public class MessageHeader {
	public int magic_code;			// 0x5a5aa5a5
    public int checksum;			// all data checksum
	public short msg_type;			// message type
	public short data_len;			// valid data length
	public short reserved;

	public MessageHeader() {
		magic_code = 0x5a5aa5a5;
		checksum = 0x0;
		msg_type = 0x0b03;
		data_len = 0x0;
        reserved = 0x0;
	}
}
