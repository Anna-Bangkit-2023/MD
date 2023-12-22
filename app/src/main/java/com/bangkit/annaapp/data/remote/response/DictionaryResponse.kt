package com.bangkit.annaapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class DictionaryResponse(

	@field:SerializedName("license")
	val license: License,

	@field:SerializedName("phonetic")
	val phonetic: String,

	@field:SerializedName("phonetics")
	val phonetics: List<PhoneticsItem>,

	@field:SerializedName("word")
	val word: String,

	@field:SerializedName("meanings")
	val meanings: List<MeaningsItem>,

	@field:SerializedName("sourceUrls")
	val sourceUrls: List<String>
)

data class MeaningsItem(

	@field:SerializedName("synonyms")
	val synonyms: List<String>,

	@field:SerializedName("partOfSpeech")
	val partOfSpeech: String,

	@field:SerializedName("antonyms")
	val antonyms: List<Any>,

	@field:SerializedName("definitions")
	val definitions: List<DefinitionsItem>
)

data class DefinitionsItem(

	@field:SerializedName("synonyms")
	val synonyms: List<Any>,

	@field:SerializedName("antonyms")
	val antonyms: List<Any>,

	@field:SerializedName("definition")
	val definition: String,

	@field:SerializedName("example")
	val example: String
)

data class License(

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("url")
	val url: String
)

data class PhoneticsItem(

	@field:SerializedName("sourceUrl")
	val sourceUrl: String,

	@field:SerializedName("license")
	val license: License,

	@field:SerializedName("text")
	val text: String,

	@field:SerializedName("audio")
	val audio: String
)
