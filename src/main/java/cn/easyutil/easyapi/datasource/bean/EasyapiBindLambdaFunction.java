package cn.easyutil.easyapi.datasource.bean;

import java.io.Serializable;
import java.util.function.Function;

public interface EasyapiBindLambdaFunction<T, R> extends Function<T, R>, Serializable {

}
