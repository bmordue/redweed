import React, { useState } from 'react';

function App() {
  const [file, setFile] = useState(null);
  const [mediaType, setMediaType] = useState('book');
  const [dataType, setDataType] = useState('event');
  const [data, setData] = useState('');

  const handleFileChange = (event) => {
    setFile(event.target.files[0]);
  };

  const handleMediaTypeChange = (event) => {
    setMediaType(event.target.value);
  };

  const handleDataTypeChange = (event) => {
    setDataType(event.target.value);
  };

  const handleDataChange = (event) => {
    setData(event.target.value);
  };

  const handleFileUploadSubmit = async (event) => {
    event.preventDefault();
    if (!file) {
      alert('Please select a file!');
      return;
    }

    const formData = new FormData();
    formData.append('file', file);

    const endpointMap = {
      book: '/books',
      media: '/media',
      music: '/music',
    };
    const endpoint = endpointMap[mediaType];

    try {
      const response = await fetch(endpoint, {
        method: 'POST',
        body: formData,
      });

      if (response.ok) {
        alert('File uploaded successfully!');
      } else {
        alert('File upload failed!');
      }
    } catch (error) {
      console.error('Error uploading file:', error);
      alert('An error occurred while uploading the file.');
    }
  };

  const handleDataSubmit = async (event) => {
    event.preventDefault();
    if (!data) {
      alert('Please enter some data!');
      return;
    }

    const endpointMap = {
      event: '/events',
      person: '/persons',
      place: '/places',
      review: '/reviews',
      ttl: '/ttl',
    };
    const endpoint = endpointMap[dataType];

    try {
      const response = await fetch(endpoint, {
        method: 'POST',
        headers: {
          'Content-Type': 'text/plain',
        },
        body: data,
      });

      if (response.ok) {
        alert('Data submitted successfully!');
      } else {
        alert('Data submission failed!');
      }
    } catch (error) {
      console.error('Error submitting data:', error);
      alert('An error occurred while submitting the data.');
    }
  };

  return (
    <main className="container">
      <h1>Jweed Frontend</h1>
      <article>
        <h2>File Upload</h2>
        <form onSubmit={handleFileUploadSubmit}>
          <label htmlFor="mediaType">Media Type</label>
          <select id="mediaType" value={mediaType} onChange={handleMediaTypeChange}>
            <option value="book">Book (EPUB)</option>
            <option value="media">Media (MP4)</option>
            <option value="music">Music (MP3)</option>
          </select>
          <label htmlFor="file">File</label>
          <input type="file" id="file" onChange={handleFileChange} />
          <button type="submit">Upload</button>
        </form>
      </article>

      <article>
        <h2>Data Submission</h2>
        <form onSubmit={handleDataSubmit}>
          <label htmlFor="dataType">Data Type</label>
          <select id="dataType" value={dataType} onChange={handleDataTypeChange}>
            <option value="event">Event</option>
            <option value="person">Person</option>
            <option value="place">Place</option>
            <option value="review">Review</option>
            <option value="ttl">TTL</option>
          </select>
          <label htmlFor="data">Data</label>
          <textarea id="data" value={data} onChange={handleDataChange} />
          <button type="submit">Submit</button>
        </form>
      </article>
    </main>
  );
}

export default App;
