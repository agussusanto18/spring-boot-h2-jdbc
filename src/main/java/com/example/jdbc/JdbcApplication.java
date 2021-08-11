package com.example.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class JdbcApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(JdbcApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(JdbcApplication.class, args);
	}

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public void run(String... args) throws Exception {
		log.info("Creating Table");

		jdbcTemplate.execute("DROP TABLE customers IF EXISTS");
		jdbcTemplate.execute("CREATE TABLE customers(id SERIAL, first_name VARCHAR(255), last_name VARCHAR(255))");

		List<Object[]> splitUpName = Arrays.asList("Agus s", "Tegar Bp", "Ardi Maulana", "Andi Arsa", "Muhammad aldo", "Chen Long")
				.stream().map(name -> name.split(" "))
				.collect(Collectors.toList());

		splitUpName.forEach(name -> log.info(String.format("Inserting customers record for %s %s", name[0], name[1])));

		jdbcTemplate.batchUpdate("INSERT INTO customers(first_name, last_name) VALUES(?, ?)", splitUpName);

		log.info("Querying for customer records where first_name = 'Agus':");
		jdbcTemplate.query("SELECT id, first_name, last_name FROM customers WHERE first_name = ?", new Object[]{"Agus"}, (rs, rowNum) -> new Customer(rs.getLong("id"), rs.getString("first_name"), rs.getString("last_name")))
				.forEach(customer -> log.info(customer.toString()));
	}
}
