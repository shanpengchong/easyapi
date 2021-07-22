package cn.easyutil.easyapi.filter.model;

import cn.easyutil.easyapi.entity.common.ApidocComment;
import cn.easyutil.easyapi.entity.common.BodyType;
import cn.easyutil.easyapi.filter.ReadInterfaceApiFilter;
import cn.easyutil.easyapi.util.SpringUtil;
import cn.easyutil.easyapi.util.StringUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 读取接口方法相关的api
 */
public class DefaultReadInterfaceApi implements ReadInterfaceApiFilter {


    @Override
    public boolean readInterfaceIgnore(Method method) {
        ApidocComment apidocComment = method.getDeclaredAnnotation(ApidocComment.class);
        if(apidocComment != null){
            boolean hidden = apidocComment.hidden();
            if(hidden){
                return true;
            }
            return apidocComment.ignore();
        }
        ApiOperation apiOperation = method.getDeclaredAnnotation(ApiOperation.class);
        if(apiOperation != null){
            return apiOperation.hidden();
        }
        return false;
    }

    @Override
    public BodyType readInterfaceBodyType(Method method) {
        Parameter[] parameters = method.getParameters();
        if (parameters == null || parameters.length == 0) {
            // 无入参
            return BodyType.FORM;
        }
        for (Parameter parameter : parameters) {
            RequestBody annotation = parameter.getAnnotation(RequestBody.class);
            if (annotation != null) {
                return BodyType.JSON;
            }
            // 文件上传
            if (parameter.getType().getCanonicalName().equals(MultipartFile.class.getCanonicalName())
                    || parameter.getType().getCanonicalName().equals(MultipartFile[].class.getCanonicalName())) {
                return BodyType.FILE;
            }
        }
        return BodyType.FORM;
    }

    @Override
    public String readInterfaceRequestPath(Method method) {
        String mapping = SpringUtil.getMapping(method);
        if (!StringUtil.isEmpty(mapping)) {
            return parseUrl(mapping);
        }
        return null;
    }

    @Override
    public String readInterfaceAliasName(Method method) {
        Annotation methodApi = method.getDeclaredAnnotation(ApidocComment.class);
        if(methodApi != null){
            ApidocComment api = (ApidocComment) methodApi;
            if(!StringUtil.isEmpty( api.value())){
                return api.value();
            }
        }
        Annotation an = method.getDeclaredAnnotation(ApiOperation.class);
        if (an != null) {
            ApiOperation api = (ApiOperation) an;
            if(!StringUtil.isEmpty(api.value())){
                return api.value();
            }
        }
        return null;
    }

    @Override
    public String readInterfaceComments(Method method) {
        ApidocComment apidocComment = method.getDeclaredAnnotation(ApidocComment.class);
        if(apidocComment != null){
            return apidocComment.notes();
        }
        ApiOperation apiOperation = method.getDeclaredAnnotation(ApiOperation.class);
        if(apiOperation != null && !StringUtil.isEmpty(apiOperation.notes())){
            return apiOperation.notes();
        }else if(apiOperation != null && !StringUtil.isEmpty(apiOperation.tags())){
            String[] tags = apiOperation.tags();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < tags.length; i++) {
                sb.append(i+1+":"+tags[i]+".");
            }
            return sb.toString();
        }
        return "";
    }

    @Override
    public String readRequestNotes(Method method) {
        ApidocComment apidocComment = method.getDeclaredAnnotation(ApidocComment.class);
        if(!StringUtil.isEmpty(apidocComment) && !StringUtil.isEmpty(apidocComment.requestNotes())){
            return apidocComment.requestNotes();
        }
        ApiParam apiParam = method.getDeclaredAnnotation(ApiParam.class);
        if(!StringUtil.isEmpty(apiParam) && !StringUtil.isEmpty(apiParam.value())){
            return apiParam.value();
        }
        ApiOperation apiOperation = method.getDeclaredAnnotation(ApiOperation.class);
        if(apiOperation != null && !StringUtil.isEmpty(apiOperation.notes())){
            return apiOperation.notes();
        }else if(apiOperation != null && !StringUtil.isEmpty(apiOperation.tags())){
            String[] tags = apiOperation.tags();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < tags.length; i++) {
                sb.append(i+1+":"+tags[i]+".");
            }
            return sb.toString();
        }
        return "参照字段说明";
    }

    @Override
    public String readRequestParamComments(Parameter parameter) {
        ApidocComment api = parameter.getDeclaredAnnotation(ApidocComment.class);
        if(api == null){
            return null;
        }
        return api.value();
    }

    @Override
    public String readRequestParamMockValue(Parameter parameter) {
        ApidocComment api = parameter.getDeclaredAnnotation(ApidocComment.class);
        if(api == null){
            return null;
        }
        return api.mockValue();
    }

    @Override
    public String readRequestParamName(Parameter parameter) {
        RequestParam param = parameter.getDeclaredAnnotation(RequestParam.class);
        if(param == null){
            return null;
        }
        if (!StringUtil.isEmpty(param.value())) {
            return param.value();
        }
        if (!StringUtil.isEmpty(param.defaultValue())) {
            return param.defaultValue();
        }
        return null;
    }

    @Override
    public String readResponseNotes(Method method) {
        ApidocComment apidocComment = method.getDeclaredAnnotation(ApidocComment.class);
        if(apidocComment != null){
            return apidocComment.responseNotes();
        }
        return "参照字段说明";
    }

    /**
     * 完善请求路径
     *
     * @param requestUrl
     * @return
     */
    private String parseUrl(String requestUrl) {
        if (!requestUrl.startsWith("/")) {
            requestUrl = "/" + requestUrl;
        }
        if (requestUrl.endsWith("/")) {
            requestUrl = requestUrl.substring(0, requestUrl.length() - 1);
        }
        return requestUrl;
    }
}
