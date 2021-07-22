package cn.easyutil.easyapi.parameterized;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 泛型与真实类型绑定关系
 */
public class ParameterizedTypeBind {

    private Map<String,Type> bind = new HashMap<>();

    public Type getRawType(String parameterizedTypeName){
        return bind.get(parameterizedTypeName);
    }

    public void bind(String parameterizedTypeName,Type parameterizedType){
        bind.put(parameterizedTypeName,parameterizedType);
    }

    public void bind(Map<String,Type> bind){
        this.bind.putAll(bind);
        Map<String,Type> copy = new HashMap<>();
        copy.putAll(this.bind);
        Iterator<Map.Entry<String, Type>> iterator = this.bind.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, Type> next = iterator.next();
            Type value = next.getValue();
            String key = next.getKey();
            if(copy.containsKey(value.toString())){
                copy.put(key,copy.get(value.toString()));
            }
        }
        this.bind = copy;
    }


    public void bind(ParameterizedTypeBind bind){
        if(bind == null){
            return ;
        }
        bind(bind.bind);
    }

    public Set<String> keys(){
        return bind.keySet();
    }
}
