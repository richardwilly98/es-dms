package test.github.richardwilly98.shiro;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.apache.shiro.guice.web.ShiroWebModule;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;

import com.github.richardwilly98.shiro.EsAuthenticationFilter;
import com.github.richardwilly98.shiro.EsRealm;
import com.github.richardwilly98.shiro.EsSessionDAO;
import com.github.richardwilly98.shiro.EsWebSessionManager;
import com.google.inject.Key;
import com.google.inject.Scopes;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.name.Names;

public class TestEsShiroWebModule extends ShiroWebModule {

	private final String securityFilterPath;
	private final Logger log = Logger.getLogger(this.getClass());
	
	public TestEsShiroWebModule(ServletContext servletContext, String securityFilterPath) {
		super(servletContext);
		this.securityFilterPath = securityFilterPath;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void configureShiroWeb() {
		log.debug("*** configureShiroWeb ***");
		bindConstant().annotatedWith(Names.named("shiro.loginUrl")).to("/#/login");
		
		bindRealm().to(EsRealm.class).asEagerSingleton();
		bind(SessionDAO.class).to(EsSessionDAO.class);
		bind(EsSessionDAO.class);
		
		addFilterChain("/auth/**", ANON);
//		addFilterChain("/auth/**", SSL);

		addFilterChain("/**", Key.get(EsAuthenticationFilter.class));
	}
	
	@Override
	protected void bindSessionManager(
			AnnotatedBindingBuilder<SessionManager> bind) {
		bind.to(EsWebSessionManager.class).in(Scopes.SINGLETON);
		bindConstant().annotatedWith(Names.named("shiro.globalSessionTimeout")).to(30000L);
		bind(EsWebSessionManager.class);
		
	}
	
}
