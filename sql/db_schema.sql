--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.4
-- Dumped by pg_dump version 9.6.3

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: agent_deals; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE agent_deals (
    deal_id integer NOT NULL,
    deal_agent integer,
    deal_min_kg real,
    deal_max_kg real,
    deal_min_m3 real,
    deal_max_m3 real,
    deal_min_pallets integer,
    deal_max_pallets integer,
    deal_procentual_discount double precision,
    deal_fixed_discount double precision,
    deal_quota_reached_before date,
    deal_group bigint,
    deal_group_tier integer
);


--
-- Name: agent_deals_deal_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE agent_deals_deal_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: agent_deals_deal_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE agent_deals_deal_id_seq OWNED BY agent_deals.deal_id;


--
-- Name: agents; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE agents (
    agent_id integer NOT NULL,
    agent_name character varying(64),
    agent_code character varying(16),
    agent_headquarters_country_code character varying(4)
);


--
-- Name: agents_agent_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE agents_agent_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: agents_agent_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE agents_agent_id_seq OWNED BY agents.agent_id;


--
-- Name: locations; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE locations (
    location_id bigint NOT NULL,
    location_lat double precision,
    location_lon double precision,
    location_desc character varying(128)
);


--
-- Name: locations_location_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE locations_location_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: locations_location_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE locations_location_id_seq OWNED BY locations.location_id;


--
-- Name: transport_lines; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE transport_lines (
    line_to integer,
    line_id integer NOT NULL,
    line_from integer,
    line_distance double precision,
    line_code character varying(16),
    line_modality character varying(8)
);


--
-- Name: transport_lines_line_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE transport_lines_line_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: transport_lines_line_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE transport_lines_line_id_seq OWNED BY transport_lines.line_id;


--
-- Name: transport_offers; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE transport_offers (
    offer_id bigint NOT NULL,
    offer_agent integer,
    offer_description text,
    offer_line integer,
    offer_min_weight real,
    offer_max_weight real,
    offer_min_volume real,
    offer_max_volume real,
    offer_min_pallets integer,
    offer_max_pallets integer,
    offer_cost_base double precision,
    offer_cost_per_kg double precision,
    offer_cost_per_m3 double precision,
    offer_cost_per_pallet double precision,
    offer_duration_hours integer,
    offer_required_categories character varying(16),
    offer_rejected_categories character varying(16)
);


--
-- Name: transport_offers_offer_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE transport_offers_offer_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: transport_offers_offer_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE transport_offers_offer_id_seq OWNED BY transport_offers.offer_id;


--
-- Name: transport_solutions_alt; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE transport_solutions_alt (
    solution_id bigint NOT NULL,
    solution_has_road boolean,
    solution_has_ferry boolean,
    solution_next_stop integer,
    solution_from integer,
    solution_to integer,
    solution_next_offer bigint,
    solution_num_offers smallint,
    solution_duration_hours integer,
    solution_cost_base double precision,
    solution_cost_per_kg double precision,
    solution_cost_per_m3 double precision,
    solution_cost_per_pallet double precision,
    solution_min_weight real,
    solution_max_weight real,
    solution_min_volume real,
    solution_max_volume real,
    solution_min_pallets real,
    solution_max_pallets real,
    solution_required_categories character varying(16),
    solution_rejected_categories character varying(16)
);


--
-- Name: transport_solutions_solution_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE transport_solutions_solution_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: transport_solutions_solution_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE transport_solutions_solution_id_seq OWNED BY transport_solutions_alt.solution_id;


--
-- Name: transport_solutions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE transport_solutions (
    solution_id bigint DEFAULT nextval('transport_solutions_solution_id_seq'::regclass) NOT NULL,
    solution_has_road boolean,
    solution_has_ferry boolean,
    solution_next_stop integer,
    solution_from integer,
    solution_to integer,
    solution_next_offer bigint,
    solution_num_offers smallint,
    solution_duration_hours integer,
    solution_cost_base double precision,
    solution_cost_per_kg double precision,
    solution_cost_per_m3 double precision,
    solution_cost_per_pallet double precision,
    solution_min_weight real,
    solution_max_weight real,
    solution_min_volume real,
    solution_max_volume real,
    solution_min_pallets real,
    solution_max_pallets real,
    solution_required_categories character varying(16),
    solution_rejected_categories character varying(16)
);


--
-- Name: agent_deals deal_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY agent_deals ALTER COLUMN deal_id SET DEFAULT nextval('agent_deals_deal_id_seq'::regclass);


--
-- Name: agents agent_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY agents ALTER COLUMN agent_id SET DEFAULT nextval('agents_agent_id_seq'::regclass);


--
-- Name: locations location_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY locations ALTER COLUMN location_id SET DEFAULT nextval('locations_location_id_seq'::regclass);


--
-- Name: transport_lines line_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY transport_lines ALTER COLUMN line_id SET DEFAULT nextval('transport_lines_line_id_seq'::regclass);


--
-- Name: transport_offers offer_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY transport_offers ALTER COLUMN offer_id SET DEFAULT nextval('transport_offers_offer_id_seq'::regclass);


--
-- Name: transport_solutions_alt solution_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY transport_solutions_alt ALTER COLUMN solution_id SET DEFAULT nextval('transport_solutions_solution_id_seq'::regclass);


--
-- Name: agent_deals agent_deals_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY agent_deals
    ADD CONSTRAINT agent_deals_pkey PRIMARY KEY (deal_id);


--
-- Name: agents agents_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY agents
    ADD CONSTRAINT agents_pkey PRIMARY KEY (agent_id);


--
-- Name: locations locations_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY locations
    ADD CONSTRAINT locations_pkey PRIMARY KEY (location_id);


--
-- Name: transport_lines transport_lines_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY transport_lines
    ADD CONSTRAINT transport_lines_pkey PRIMARY KEY (line_id);


--
-- Name: transport_offers transport_offers_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY transport_offers
    ADD CONSTRAINT transport_offers_pkey PRIMARY KEY (offer_id);


--
-- Name: transport_solutions transport_solutions_alt_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY transport_solutions
    ADD CONSTRAINT transport_solutions_alt_pkey PRIMARY KEY (solution_id);


--
-- Name: transport_solutions_alt transport_solutions_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY transport_solutions_alt
    ADD CONSTRAINT transport_solutions_pkey PRIMARY KEY (solution_id);


--
-- PostgreSQL database dump complete
--

