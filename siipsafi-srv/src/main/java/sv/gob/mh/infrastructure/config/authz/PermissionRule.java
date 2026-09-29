package sv.gob.mh.infrastructure.config.authz;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO que representa una regla de permiso devuelta por el servicio de autorización.
 */
public class PermissionRule {

    @JsonProperty("id")
    private String id;

    @JsonProperty("groupId")
    private String groupId;

    @JsonProperty("componentId")
    private String componentId;

    @JsonProperty("resourcePath")
    private String resourcePath;

    @JsonProperty("resourceName")
    private String resourceName;

    @JsonProperty("operationName")
    private String operationName;

    @JsonProperty("effect")
    private String effect;

    @JsonProperty("conditions")
    private String conditions;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public String getConditions() {
        return conditions;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }
}
