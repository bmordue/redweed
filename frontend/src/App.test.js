import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import App from "./App";

// Mock fetch for API call tests
global.fetch = jest.fn();

// Mock alert function
global.alert = jest.fn();

describe("App Component", () => {
  beforeEach(() => {
    fetch.mockClear();
    alert.mockClear();
  });

  // Basic rendering tests
  test("renders main heading", () => {
    render(<App />);
    const headingElement = screen.getByText(/Jweed Frontend/i);
    expect(headingElement).toBeInTheDocument();
  });

  test("renders file upload section", () => {
    render(<App />);
    expect(screen.getByText("File Upload")).toBeInTheDocument();
    expect(screen.getByLabelText("Media Type")).toBeInTheDocument();
    expect(screen.getByLabelText("File")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Upload" })).toBeInTheDocument();
  });

  test("renders data submission section", () => {
    render(<App />);
    expect(screen.getByText("Data Submission")).toBeInTheDocument();
    expect(screen.getByLabelText("Data Type")).toBeInTheDocument();
    expect(screen.getByLabelText("Data")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Submit" })).toBeInTheDocument();
  });

  // Media type dropdown tests
  test("media type dropdown has correct options", () => {
    render(<App />);

    expect(screen.getByDisplayValue("Book (EPUB)")).toBeInTheDocument();
    expect(
      screen.getByRole("option", { name: "Book (EPUB)" })
    ).toBeInTheDocument();
    expect(
      screen.getByRole("option", { name: "Media (MP4)" })
    ).toBeInTheDocument();
    expect(
      screen.getByRole("option", { name: "Music (MP3)" })
    ).toBeInTheDocument();
  });

  test("can change media type selection", async () => {
    render(<App />);
    const mediaTypeSelect = screen.getByLabelText("Media Type");

    await userEvent.selectOptions(mediaTypeSelect, "media");
    expect(screen.getByDisplayValue("Media (MP4)")).toBeInTheDocument();

    await userEvent.selectOptions(mediaTypeSelect, "music");
    expect(screen.getByDisplayValue("Music (MP3)")).toBeInTheDocument();
  });

  // Data type dropdown tests
  test("data type dropdown has correct options", () => {
    render(<App />);

    expect(screen.getByDisplayValue("Event")).toBeInTheDocument();
    expect(screen.getByRole("option", { name: "Event" })).toBeInTheDocument();
    expect(screen.getByRole("option", { name: "Person" })).toBeInTheDocument();
    expect(screen.getByRole("option", { name: "Place" })).toBeInTheDocument();
    expect(screen.getByRole("option", { name: "Review" })).toBeInTheDocument();
    expect(screen.getByRole("option", { name: "TTL" })).toBeInTheDocument();
  });

  test("can change data type selection", async () => {
    render(<App />);
    const dataTypeSelect = screen.getByLabelText("Data Type");

    await userEvent.selectOptions(dataTypeSelect, "person");
    expect(screen.getByDisplayValue("Person")).toBeInTheDocument();

    await userEvent.selectOptions(dataTypeSelect, "place");
    expect(screen.getByDisplayValue("Place")).toBeInTheDocument();
  });

  // File input tests
  test("can select a file", async () => {
    render(<App />);
    const fileInput = screen.getByLabelText("File");

    const file = new File(["test content"], "test.epub", {
      type: "application/epub+zip",
    });
    await userEvent.upload(fileInput, file);

    expect(fileInput.files[0]).toBe(file);
    expect(fileInput.files).toHaveLength(1);
  });

  // Data textarea tests
  test("can enter data in textarea", async () => {
    render(<App />);
    const dataTextarea = screen.getByLabelText("Data");

    await userEvent.type(dataTextarea, "Test event data");
    expect(dataTextarea).toHaveValue("Test event data");
  });

  // Form validation tests
  test("shows alert when trying to upload without selecting file", async () => {
    render(<App />);
    const uploadButton = screen.getByRole("button", { name: "Upload" });

    await userEvent.click(uploadButton);

    expect(alert).toHaveBeenCalledWith("Please select a file!");
  });

  test("shows alert when trying to submit without entering data", async () => {
    render(<App />);
    const submitButton = screen.getByRole("button", { name: "Submit" });

    await userEvent.click(submitButton);

    expect(alert).toHaveBeenCalledWith("Please enter some data!");
  });

  // Successful file upload tests
  test.each([
    {
      mediaType: "book",
      endpoint: "/books",
      file: new File(["test content"], "test.epub", {
        type: "application/epub+zip",
      }),
    },
    {
      mediaType: "media",
      endpoint: "/media",
      file: new File(["test content"], "test.mp4", { type: "video/mp4" }),
    },
    {
      mediaType: "music",
      endpoint: "/music",
      file: new File(["test content"], "test.mp3", { type: "audio/mp3" }),
    },
  ])(
    "successful file upload for $mediaType",
    async ({ mediaType, endpoint, file }) => {
      fetch.mockResolvedValueOnce({ ok: true });
      render(<App />);
      const fileInput = screen.getByLabelText("File");
      const mediaTypeSelect = screen.getByLabelText("Media Type");
      const uploadButton = screen.getByRole("button", { name: "Upload" });
      await userEvent.upload(fileInput, file);
      await userEvent.selectOptions(mediaTypeSelect, mediaType);
      await userEvent.click(uploadButton);
      await waitFor(() => {
        expect(fetch).toHaveBeenCalledWith(endpoint, {
          method: "POST",
          body: expect.any(FormData),
        });
      });
      expect(alert).toHaveBeenCalledWith("File uploaded successfully!");
    }
  );

  // Failed file upload tests
  test("handles failed file upload", async () => {
    fetch.mockResolvedValueOnce({ ok: false });

    render(<App />);
    const fileInput = screen.getByLabelText("File");
    const uploadButton = screen.getByRole("button", { name: "Upload" });

    const file = new File(["test content"], "test.epub", {
      type: "application/epub+zip",
    });
    await userEvent.upload(fileInput, file);
    await userEvent.click(uploadButton);

    await waitFor(() => {
      expect(alert).toHaveBeenCalledWith("File upload failed!");
    });
  });

  test("handles file upload network error", async () => {
    fetch.mockRejectedValueOnce(new Error("Network error"));

    render(<App />);
    const fileInput = screen.getByLabelText("File");
    const uploadButton = screen.getByRole("button", { name: "Upload" });

    const file = new File(["test content"], "test.epub", {
      type: "application/epub+zip",
    });
    await userEvent.upload(fileInput, file);
    await userEvent.click(uploadButton);

    await waitFor(() => {
      expect(alert).toHaveBeenCalledWith(
        "An error occurred while uploading the file."
      );
    });
  });

  // Successful data submission tests
  test.each([
    { dataType: "event", endpoint: "/events", data: "Test event data" },
    { dataType: "person", endpoint: "/persons", data: "Test person data" },
    { dataType: "place", endpoint: "/places", data: "Test place data" },
    { dataType: "review", endpoint: "/reviews", data: "Test review data" },
    { dataType: "ttl", endpoint: "/ttl", data: "Test TTL data" },
  ])(
    "successful data submission for $dataType",
    async ({ dataType, endpoint, data }) => {
      fetch.mockResolvedValueOnce({ ok: true });
      render(<App />);
      const dataTextarea = screen.getByLabelText("Data");
      const dataTypeSelect = screen.getByLabelText("Data Type");
      const submitButton = screen.getByRole("button", { name: "Submit" });
      await userEvent.type(dataTextarea, data);
      await userEvent.selectOptions(dataTypeSelect, dataType);
      await userEvent.click(submitButton);
      await waitFor(() => {
        expect(fetch).toHaveBeenCalledWith(endpoint, {
          method: "POST",
          headers: {
            "Content-Type": "text/plain",
          },
          body: data,
        });
      });
      expect(alert).toHaveBeenCalledWith("Data submitted successfully!");
    }
  );

  // Failed data submission tests
  test("handles failed data submission", async () => {
    fetch.mockResolvedValueOnce({ ok: false });

    render(<App />);
    const dataTextarea = screen.getByLabelText("Data");
    const submitButton = screen.getByRole("button", { name: "Submit" });

    await userEvent.type(dataTextarea, "Test data");
    await userEvent.click(submitButton);

    await waitFor(() => {
      expect(alert).toHaveBeenCalledWith("Data submission failed!");
    });
  });

  test("handles data submission network error", async () => {
    fetch.mockRejectedValueOnce(new Error("Network error"));

    render(<App />);
    const dataTextarea = screen.getByLabelText("Data");
    const submitButton = screen.getByRole("button", { name: "Submit" });

    await userEvent.type(dataTextarea, "Test data");
    await userEvent.click(submitButton);

    await waitFor(() => {
      expect(alert).toHaveBeenCalledWith(
        "An error occurred while submitting the data."
      );
    });
  });
});
