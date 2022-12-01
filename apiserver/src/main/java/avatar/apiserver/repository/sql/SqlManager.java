package avatar.apiserver.repository.sql;

import java.util.Map;

public class SqlManager {
    public static final String savePhotoUrl = 
        "INSERT INTO avatar(id, sexuality, photo_url, photo_uploaded_at) " +
                "VALUES (:id, :sexuality, :photoUrl, :photoUploadedAt) ";

    public static final String updateStatus = 
        "UPDATE avatar "         +
        "SET status = :status "  +
        "WHERE id = :id ";

    public static final String updateToonify = 
        "UPDATE avatar "                                 +
        "SET toonify_url = :toonifyUrl, "                +
            "toonified_at = :toonifiedAt "  +
        "WHERE id = :id";

    public static final String updateHead = 
        "UPDATE avatar "                                 +
        "SET head_url = :headUrl, "                      +
            "head_created_at = :headCreatedAt "        +
        "WHERE id = :id";

    public static final String updateObject =
        "UPDATE avatar "                                 +    
        "SET object_url = :objectUrl, "                  +
            "object_created_at = :objectCreatedAt "      +    
        "WHERE id = :id" ;                                 
    
    public static final String findById = 
        "SELECT id,"                   +
               "sexuality,"            +
               "photo_url,"            +
               "toonify_url,"          + 
               "head_url,"             +   
               "object_url,"           +    
               "status,"               +
               "photo_uploaded_at,"    +            
               "toonified_at,"         +            
               "head_created_at,"      +        
               "object_created_at "    +            
        "FROM avatar "                 +
        "WHERE id = :id";


    public static final String findBodys =
        "SELECT num, fbx_url, img_url "   +
        "FROM body_info "                 +
        "WHERE sexuality = :sexuality ";    

    public static final String findHairs =
        "SELECT num, fbx_url, img_url "   +
        "FROM hair_info "                 +
        "WHERE sexuality = :sexuality";    

    public static final String findStyles =
        "SELECT code, img_url, description, recommend " +
        "FROM style "                                   +
        "ORDER BY priority DESC";

    public static final String createOrder =
     "INSERT INTO workqueue (id, status, photo_url, style_code) VALUE (:id, :status, :photo_url, :style_code)";

    public static final String getNextOrder =
     "SELECT id, photo_url, style_code FROM workqueue WHERE registered_at = (SELECT min(registered_at) FROM workqueue WHERE status='waiting')";

    public static final String getOrderStatus =
     "SELECT status FROM workqueue WHERE id = :id";

    public static final String setOrderStatusWaiting = 
     "UPDATE workqueue SET status='waiting', loaded_at=:time WHERE id=:id";

    public static final String setOrderStatusLoading = 
     "UPDATE workqueue SET status='loading', loaded_at=:time WHERE id=:id";

    public static final String setOrderStatusComplete = 
    "UPDATE workqueue SET status='complete', completed_at=:time WHERE id=:id";

    public static final String checkGPUStatus =
    "SELECT num, status FROM gpu_working";

    public static final String updateGPUStatus = 
    "UPDATE gpu_working SET status=:status WHERE num=:num";

    public static final String readCache = 
    "SELECT object_url FROM cache WHERE id=:id AND body_num=:body_num AND hair_num=:hair_num"; 

    public static final String writeCache = 
    "INSERT INTO cache (id, body_num, hair_num, object_url) VALUE (:id, :body_num, :hair_num, :object_url)"; 

    public static final Map<String, String> orderTimeType = Map.of("loading", "loaded_at", "complete", "completed_at");
}
