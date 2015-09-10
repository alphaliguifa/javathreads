

public abstract class AttachmentSO {
	
    protected String filename;

    protected Long filesize;

    protected String mimetype;

    protected byte[] content;

    public AttachmentSO() {
    }

    public String getFilename() {
        return this.filename;
    }

    public Long getFilesize() {
        return this.filesize;
    }

    public String getMimetype() {
        return this.mimetype;
    }

    public byte[] getContent() {
        return this.content;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setFilesize(Long filesize) {
        this.filesize = filesize;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("filename:[" + filename + "]\r\n");
        sb.append("filesize:[" + filesize + "]\r\n");
        sb.append("mimetype:[" + mimetype + "]\r\n");
//        sb.append("content:[" + new String(getContent()) + "]\r\n");
        return sb.toString();
    }

}
