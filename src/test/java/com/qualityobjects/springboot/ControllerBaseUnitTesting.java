package com.qualityobjects.springboot;

import com.qualityobjects.commons.utils.JsonUtils;
import com.qualityobjects.springboot.dto.PageData;
import com.qualityobjects.springboot.entity.DtoWrapper;
import com.qualityobjects.springboot.entity.EntityBase;
import com.qualityobjects.springboot.services.CRUDInterface;
import com.qualityobjects.springboot.services.PaginationInterface;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


//@ComponentScan(includeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = CustomerController.class))
//@Import({ CustomerController.class, TestAuthConfiguration.class, SecurityAOPConfig.class})  //, TestAuthConfiguration.class })
@ActiveProfiles({"test", "unittest"})
public abstract class ControllerBaseUnitTesting<T extends EntityBase<ID>, ID> {
	private static final Logger LOG = LoggerFactory.getLogger(ControllerBaseUnitTesting.class);

	// Configuration to use the mock-user
	@Profile("unittest")
	@EnableWebSecurity
	public static class TestAuthConfiguration extends WebSecurityConfigurerAdapter {

		@Override
		protected void configure(final HttpSecurity http) throws Exception {
			LOG.info("configure(HttpSecurity http) ");
			http.csrf().disable().authorizeRequests().filterSecurityInterceptorOncePerRequest(true)
					.antMatchers("/api/session/**").permitAll() //
					.antMatchers("/api/**").authenticated() //
					.anyRequest().permitAll().and() //
					.addFilterBefore(new OncePerRequestFilter() {
						@Override
						protected void doFilterInternal(final HttpServletRequest request,
								final HttpServletResponse response, final FilterChain filterChain)
								throws ServletException, IOException {
							final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
							if (auth != null) {
								LOG.info("User authenticated: {}, roles: {}, req: {} {}", auth.getName(),
										auth.getAuthorities(), request.getMethod(), request.getRequestURI());
							}
							filterChain.doFilter(request, response);
						}
					}, BasicAuthenticationFilter.class).sessionManagement()
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		}

	}

	@Value("${services.url.prefix}/")
	private String urlPrefix;

	@Getter
	private final String urlControllerBase;
	private final Object serviceMock;

	public ControllerBaseUnitTesting(final String controllerPath, final Object service) {
		urlControllerBase = "/api/" + controllerPath;
		serviceMock = service;
	}

	protected List<T> generateBeanList() {
		final int size = (int) Math.random() * 100;
		final List<T> lista = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			lista.add(getBean());
		}
		return lista;
	}

	protected abstract T getBean();

	protected abstract ID getRandomId();

	protected abstract Map<String, Object> getFieldsToMatch();
	

	@Autowired
	protected MockMvc mockMvc;

	@Test
	@WithMockUser(username = "test", authorities = { "OTHER" })
	public void testCreate_403() throws Exception {
		if (this.serviceMock instanceof CRUDInterface) {
			final T bean = getBean();
			@SuppressWarnings("unchecked")
			final CRUDInterface<T, ID> service = (CRUDInterface<T, ID>) serviceMock;
			when(service.create(bean)).thenReturn(DtoWrapper.of(bean));

			mockMvc.perform(post(this.urlControllerBase).content(JsonUtils.toJSON(bean))
					.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testCreate() throws Exception {
		if (this.serviceMock instanceof CRUDInterface) {
			final T bean = getBean();

			final CRUDInterface<T, ID> service = (CRUDInterface<T, ID>) serviceMock;
			when(service.create(any((Class<T>) bean.getClass()))).thenReturn(DtoWrapper.of(bean));
			mockMvc.perform(post(this.urlControllerBase).content(JsonUtils.toJSON(bean))
					.contentType(MediaType.APPLICATION_JSON).characterEncoding("utf8")).andExpect(status().isOk())
					.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(entityJsonValidation());
		} else {
			Assert.state(false, "Wrong test implementation");
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testModify() throws Exception {
		if (this.serviceMock instanceof CRUDInterface) {
			final T bean = getBean();
			final CRUDInterface<T, ID> service = (CRUDInterface<T, ID>) serviceMock;
			when(service.update(any((Class<T>) bean.getClass()))).thenReturn(DtoWrapper.of(bean));
			mockMvc.perform(post(String.format("%s/%s", this.urlControllerBase, bean.getId()))
					.content(JsonUtils.toJSON(bean)).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf8"))
					.andExpect(status().isOk())
					.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(entityJsonValidation());
		} else {
			Assert.state(false, "Wrong test implementation");
		}
	}

	@Test
	public void testFindById() throws Exception {
		if (this.serviceMock instanceof CRUDInterface) {
			final T bean = getBean();
			@SuppressWarnings("unchecked")
			final CRUDInterface<T, ID> service = (CRUDInterface<T, ID>) serviceMock;
			when(service.getById(bean.getId())).thenReturn(DtoWrapper.of(bean));
			mockMvc.perform(get(String.format("%s/%s", this.urlControllerBase, bean.getId()))
					.content(JsonUtils.toJSON(bean)).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf8"))
					.andExpect(status().isOk())
					.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(entityJsonValidation());
		} else {
			Assert.state(false, "Wrong test implementation");
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testFindAll() throws Exception {
		if (this.serviceMock instanceof CRUDInterface) {
			final T bean = getBean();
			final CRUDInterface<T, ID> service = (CRUDInterface<T, ID>) serviceMock;
			when(service.findAll(any(), any())).thenReturn(DtoWrapper.of(List.of(bean, bean, bean)));
			mockMvc.perform(get(String.format("%s/", this.urlControllerBase))
					.content(JsonUtils.toJSON(bean)).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf8"))
					.andExpect(status().isOk())
					.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(listJsonValidator(3));
		} else {
			Assert.state(false, "Wrong test implementation");
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testGetPage() throws Exception {
		if (this.serviceMock instanceof PaginationInterface) {
			final T bean = getBean();
			final PaginationInterface<T> service = (PaginationInterface<T>) serviceMock;
			PageData<T> pg = PageData.of(DtoWrapper.of(List.of(bean, bean, bean)), 30,null);
			when(service.getPage(any(), any(),any())).thenReturn(pg);
			mockMvc.perform(get(String.format("%s/page", this.urlControllerBase))
			.param("_page", "2")
			.param("_pageSize", "3")
			.param("_sortFields", "id")
			.param("_sortDir", "ASC")
			.content(JsonUtils.toJSON(bean)).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf8"))
					.andExpect(status().isOk())
					.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(pageJsonValidator(30, 3));
		} else {
			Assert.state(false, "Wrong test implementation");
		}
	}

	@Test
	public void testDelete() throws Exception {
		if (this.serviceMock instanceof CRUDInterface) {
			final ID idElement = getRandomId();

			mockMvc.perform(delete(String.format("%s/%s", this.urlControllerBase, idElement))
					.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
		} else {
			Assert.state(false, "Wrong test implementation");
		}
	}

	/**
	 * Validate the json list structure assuming allthe same instance in all cases.
	 * Each instance/element is validated using
	 *
	 * 
	 * @param size
	 * @return
	 */
	protected ResultMatcher listJsonValidator(final int size) {		
		if (size == 0) {
			return ResultMatcher.matchAll(jsonPath("$").isArray(), jsonPath("$.length()").isEmpty());
		}
		List<ResultMatcher> rm = new ArrayList<>();
		rm.add(jsonPath("$").isArray());
		rm.add(jsonPath("$.length()").value(size));
		for (Map.Entry<String,Object> fieldMatcher : getFieldsToMatch().entrySet()) {
			rm.add(jsonPath(String.format("$[0].%s", fieldMatcher.getKey())).value(fieldMatcher.getValue()));
			rm.add(jsonPath(String.format("$[%d].%s", size-1, fieldMatcher.getKey())).value(fieldMatcher.getValue()));
		}
		return ResultMatcher.matchAll(rm.toArray(new ResultMatcher[rm.size()]));
	} 

	protected ResultMatcher pageJsonValidator(final int total, final int pageSize) {
		List<ResultMatcher> rm = new ArrayList<>();
		rm.add(jsonPath("$.content").isArray());
		rm.add(jsonPath("$.content.length()").value(pageSize));
		rm.add(jsonPath("$.total").value(total));
		for (Map.Entry<String,Object> fieldMatcher : getFieldsToMatch().entrySet()) {
			rm.add(jsonPath(String.format("$.content[0].%s", fieldMatcher.getKey())).value(fieldMatcher.getValue()));
			rm.add(jsonPath(String.format("$.content[%d].%s", pageSize-1, fieldMatcher.getKey())).value(fieldMatcher.getValue()));
		}
		return ResultMatcher.matchAll(rm.toArray(new ResultMatcher[rm.size()]));
	}

	protected ResultMatcher entityJsonValidation() {
		List<ResultMatcher> rm = new ArrayList<>();
		
		for (Map.Entry<String,Object> fieldMatcher : getFieldsToMatch().entrySet()) {
			rm.add(jsonPath(String.format("$.%s", fieldMatcher.getKey())).value(fieldMatcher.getValue()));
		}
		return ResultMatcher.matchAll(rm.toArray(new ResultMatcher[rm.size()]));
	}

}
