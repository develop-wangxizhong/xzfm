package xzfm.domain;

import org.springframework.context.annotation.Import;
import xzfm.domain.core.InitializationJpaEntityManager;

import java.lang.annotation.*;

/**
 * Created by wangxizhong on 17/5/6.
 */

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(InitializationJpaEntityManager.class)
public @interface EnableJpaEntityManager {
}
