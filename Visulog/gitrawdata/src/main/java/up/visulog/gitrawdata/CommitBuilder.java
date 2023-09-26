package up.visulog.gitrawdata;

public class CommitBuilder {
    private final String id;
    private String author;
    private String date;
    private String description;
    private String mergedFrom;
    public boolean isMergeCommit;
    private String email;
    private int linesAdded;
    private int linesRemoved;
  
    public CommitBuilder(String id) {
        this.id = id;
        this.isMergeCommit = false;
    }
    
    public CommitBuilder setLinesAdded(int a) {
        this.linesAdded = a;
        return this;
      }
    
      public CommitBuilder setLinesRemoved(int r) {
        this.linesRemoved = r;
        return this;
      }

    public CommitBuilder setAuthor(String author) {
        this.author = author;
        return this;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public CommitBuilder setDate(String date) {
        this.date = date;
        return this;
    }

    public CommitBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public CommitBuilder setMergedFrom(String mergedFrom) {
        this.mergedFrom = mergedFrom;
        isMergeCommit = true;
        return this;
    }
   

    public Commit createCommit() {
        return new Commit(id, author, date, description, mergedFrom, email, linesAdded,linesRemoved);
    
    }
}