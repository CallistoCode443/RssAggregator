CREATE TABLE articles (
    id BIGSERIAL PRIMARY KEY,
    source_id BIGINT NOT NULL REFERENCES sources(id) ON DELETE CASCADE,
    title TEXT NOT NULL,
    description TEXT,
    url VARCHAR(2048) NOT NULL,
    published_at TIMESTAMP,
    fetched_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_articles UNIQUE (url)
);

CREATE INDEX idx_articles_sources_id ON articles(source_id);
CREATE INDEX idx_articles_published_at ON articles(published_at DESC);
CREATE INDEX idx_articles_title ON articles USING gin(to_tsvector('english', title));
