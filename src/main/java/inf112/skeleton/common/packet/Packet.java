package inf112.skeleton.common.packet;

/**
 * All packets should follow this format, to keep it simple on each end of the server & client relationship.
 */
public class Packet {
    int id;
    PacketData data;

    public Packet(int id, PacketData data){

        this.id = id;
        this.data = data;
    }

    public Packet(toServer id, PacketData data){

        this.id = id.ordinal();
        this.data = data;
    }

    public Packet(fromServer id, PacketData data){
        this.id = id.ordinal();
        this.data = data;
    }
}
