package io.github.LoucterSo.task_tracker_backend;


//@WebMvcTest
//@AutoConfigureMockMvc
//class TaskTrackerBackendApplicationTests {
//
//
//	@Autowired
//	private MockMvc mockMvc;
//
//	@Test
//	void registerShouldResponseWith201Status() throws Exception {
//
//		this.mockMvc.perform(post("/api/v1/auth/signup")
//						.content("{\n" +
//								"    \"firstName\" : \"vladosik\",\n" +
//								"    \"lastName\" : \"vladosik\",\n" +
//								"    \"email\" : \"loc11@gmail.com\",\n" +
//								"    \"password\" : \"12341234Gg\"\n" +
//								"}"))
//				.andDo(print()).andExpect(status().isCreated());
//	}
//
//	@Test
//	public void givenMovieId_whenMakingGetRequestToMovieEndpoint_thenReturnMovie() {
//
//		TaskForm taskForm = new TaskForm("");
//		Movie testMovie = new Movie(1, "movie1", "summary1");
//		when(appService.findMovie(1)).thenReturn(testMovie);
//
//		get(uri + "/movie/" + testMovie.getId()).then()
//				.assertThat()
//				.statusCode(HttpStatus.OK.value())
//				.body("id", equalTo(testMovie.getId()))
//				.body("name", equalTo(testMovie.getName()))
//				.body("synopsis", notNullValue());
//
//		Movie result = get(uri + "/movie/" + testMovie.getId()).then()
//				.assertThat()
//				.statusCode(HttpStatus.OK.value())
//				.extract()
//				.as(Movie.class);
//		assertThat(result).isEqualTo(testMovie);
//	}
//
//	@Test
//	public void whenRequestedPost_thenCreated() {
//		with().body(new Odd(5.25f, 1, 13.1f, "X"))
//				.when()
//				.request("POST", "/odds/new")
//				.then()
//				.statusCode(201);
//	}
//
//
//	@Test
//	public void givenMovie_whenMakingPostRequestToMovieEndpoint_thenCorrect() {
//		Map<String, String> request = new HashMap<>();
//		request.put("id", "11");
//		request.put("name", "movie1");
//		request.put("synopsis", "summary1");
//
//		int movieId = given().contentType("application/json")
//				.body(request)
//				.when()
//				.post(uri + "/movie")
//				.then()
//				.assertThat()
//				.statusCode(HttpStatus.CREATED.value())
//				.extract()
//				.path("id");
//		assertThat(movieId).isEqualTo(11);
//	}
//
//	@Test
//	public void whenCallingMoviesEndpoint_thenReturnAllMovies() {
//
//		Set<Movie> movieSet = new HashSet<>();
//		movieSet.add(new Movie(1, "movie1", "summary1"));
//		movieSet.add(new Movie(2, "movie2", "summary2"));
//		when(appService.getAll()).thenReturn(movieSet);
//
//		get(uri + "/movies").then()
//				.statusCode(HttpStatus.OK.value())
//				.assertThat()
//				.body("size()", is(2));
//
//		Movie[] movies = get(uri + "/movies").then()
//				.statusCode(200)
//				.extract()
//				.as(Movie[].class);
//		assertThat(movies.length).isEqualTo(2);
//	}
//
//}
