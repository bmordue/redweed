# How-to Guides

This section provides solutions to common problems.

## Adding a new media type

To add a new media type, you will need to do the following:

1.  **Define the vocabulary.** Choose an existing RDF vocabulary or create a new one to represent the new media type. Add the vocabulary to the `docs/data-model.md` file.
2.  **Create an import parser.** Create a new class that implements the `ImportParser` interface. This class will be responsible for parsing the new media type and converting it to RDF.
3.  **Update the `MediaTypeController`** Add a new endpoint to the `MediaTypeController` to handle the new media type. This endpoint should use the new import parser to parse the media type and add it to the RDF store.
4.  **Add a new API endpoint.** Add a new endpoint to the `ApiController` to expose the new media type.
5.  **Update the documentation.** Add a new section to the `docs/API.md` file to document the new endpoint.
