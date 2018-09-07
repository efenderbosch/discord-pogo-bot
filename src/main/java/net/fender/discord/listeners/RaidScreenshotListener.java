package net.fender.discord.listeners;

//@Component
public class RaidScreenshotListener {
    //extends BaseEventListener<MessageReceivedEvent> {

//    private static final Logger LOG = LoggerFactory.getLogger(RaidScreenshotListener.class);
//
//    private static final DateTimeFormatter HH_MM_SS = DateTimeFormatter.ofPattern("HH:mm:ss");
//    private static final Pattern CURRENT_TIME_WITH_AM_PM = Pattern.compile(".*([\\s012][0-9]:[0-5][0-9]\\s[AP]M).*");
//    // private static final Pattern CURRENT_TIME_24 = Pattern.compile(".*([\\s012][0-9]:[0-5][0-9]).*");
//    private static final DateTimeFormatter HH_MM_AM_PM = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
//    private static final DateTimeFormatter HH_MM_24 = DateTimeFormatter.ofPattern("HH:mm");
//    private static final Pattern REMAINING_TIME = Pattern.compile("([01234]:[0-5][0-9]:[0-5][0-9])");
//
//    private static final RegexTextChannelCategoryFilter RAID_CHANNEL_CATEGORY_FILTER = new
//            RegexTextChannelCategoryFilter("raids");
//    private static final RegexChannelNameFilter OCR_BOT_CHANNEL_NAME_FILTER = new RegexChannelNameFilter
//            ("ocr-bot");
//    private static final EventFilter<? extends Event>[] FILTERS = new EventFilter[]{
//            TEXT_CHANNEL_FILTER,
//            RAID_CHANNEL_CATEGORY_FILTER,
//            OCR_BOT_CHANNEL_NAME_FILTER};
//
//    private final ImageAnnotatorClient vision;
//    private final PokemonRegistry pokemonRegistry;
//    private final Scheduler scheduler;
//
//    @Autowired
//    public RaidScreenshotListener(ImageAnnotatorClient imageAnnotatorClient, PokemonRegistry pokemonRegistry,
//                                  Scheduler scheduler) {
//        super(MessageReceivedEvent.class, FILTERS);
//        this.vision = imageAnnotatorClient;
//        this.pokemonRegistry = pokemonRegistry;
//        this.scheduler = scheduler;
//    }
//
//    @Override
//    public void processEvent(MessageReceivedEvent event) {
//        TextChannel textChannel = event.getTextChannel();
//        Category raidCategory = textChannel.getParent();
//        Message message = event.getMessage();
//
//        // TODO delete attachments and move to new channel ?
//
//        List<Attachment> imageAttachments = message.getAttachments().stream().
//                filter(Attachment::isImage).
//                collect(toList());
//        if (imageAttachments.isEmpty()) return;
//
//        // let the user know the bot is busy
//        textChannel.sendTyping().submit();
//        for (Attachment attachment : imageAttachments) {
//            Optional<AnnotateImageResponse> maybeResponse = performOCR(attachment);
//            if (!maybeResponse.isPresent()) continue;
//
//            AnnotateImageResponse response = maybeResponse.get();
//            String attachmentId = attachment.getId();
//            Member author = message.getMember();
//            LOG.info("checking attachment {} for a raid boss", attachmentId);
//            List<EntityAnnotation> entityAnnotations = response.getTextAnnotationsList();
//
//            Optional<Pokemon> maybePokemon = findPokemon(entityAnnotations);
//            if (!maybePokemon.isPresent()) {
//                LOG.info("no pokemon found in {}", attachmentId);
//                // TODO re-enable this if nothing found?
//                // Message reply = new MessageBuilder().
//                //      append(author.getAsMention()).
//                //      append(" I can't find a Pokémon in screenshot ").
//                //      append(attachmentId).
//                //      append('.').
//                //      build();
//                // textChannel.sendMessage(reply).submit();
//                continue;
//            }
//
//            Pokemon pokemon = maybePokemon.get();
//
//            String fullText = entityAnnotations.get(0).getDescription();
//            String[] lines = fullText.split("\\n");
//            String currentTimeString = null;
//            String location = null;
//            String remainingTimeString = null;
//            for (int i = 0; i < lines.length; i++) {
//                String line = lines[i];
//                LOG.info("{} ", line);
//                Matcher currentTimeMatcher = CURRENT_TIME_WITH_AM_PM.matcher(line);
//                if (currentTimeMatcher.matches() && currentTimeString == null) {
//                    currentTimeString = currentTimeMatcher.group(1).trim();
//                    location = lines[i + 1];
//                }
//
//                Matcher remainingTimeMatcher = REMAINING_TIME.matcher(line);
//                if (remainingTimeMatcher.matches() && remainingTimeString == null) {
//                    remainingTimeString = remainingTimeMatcher.group(1);
//                }
//            }
//
//            if (currentTimeString == null || remainingTimeString == null) {
//                MessageBuilder builder = new MessageBuilder();
//                builder.append(author.getAsMention()).append(" I couldn't figure out the:\n");
//                if (currentTimeString == null) {
//                    builder.append("current time\n");
//                }
//                if (remainingTimeString == null) {
//                    builder.append("remaining time\n");
//                }
//                Message reply = builder.append("from screenshot ").append(attachmentId).append('.').build();
//                textChannel.sendMessage(reply).submit();
//                continue;
//            }
//
//            LocalTime currentTime;
//            try {
//                currentTime = LocalTime.parse(currentTimeString, HH_MM_AM_PM);
//            } catch (DateTimeParseException e1) {
//                // full battery might be showing up as a leading "1"
//                try {
//                    currentTime = LocalTime.parse(currentTimeString.substring(1), HH_MM_AM_PM);
//                } catch (DateTimeParseException e2) {
//                    // military time?
//                    LOG.info("can't figure out time from {}", currentTimeString);
//                    MessageBuilder builder = new MessageBuilder().append(author.getAsMention()).append(" I " +
//                            "couldn't figure out the current time from ").append(currentTimeString);
//                    textChannel.sendMessage(builder.build()).submit();
//                    continue;
//                }
//            }
//            currentTime = currentTime.plusSeconds(30);
//
//            LOG.debug("current time {}", currentTime.format(HH_MM_SS));
//            String[] remainingTimeParts = remainingTimeString.split(":");
//            int hours = Integer.parseInt(remainingTimeParts[0]);
//            int minutes = Integer.parseInt(remainingTimeParts[1]);
//            int seconds = Integer.parseInt(remainingTimeParts[2]);
//            LOG.debug("remaining time {}:{}:{}", hours, minutes, seconds);
//            LocalTime despawnTime = currentTime.plusHours(hours).plusMinutes(minutes).plusSeconds(seconds);
//
//            try {
//                MessageBuilder builder = new MessageBuilder().
//                        append("Boss: ").append(pokemon.getName()).append('\n').
//                        append("Level: ").append(pokemon.getRaidLevel().level).append('\n').
//                        append("Location: ").append(location).append('\n').
//                        append("Type: ");
//                PokemonType secondaryType = pokemon.getSecondaryType();
//                if (secondaryType != null) {
//                    builder.append(pokemon.getPrimaryType().getName()).append('/').append(secondaryType.getName());
//                } else {
//                    builder.append(pokemon.getPrimaryType().getName());
//                }
//                builder.append("\nDespawns: ").append(despawnTime.format(HH_MM_AM_PM));
//
//                if (!despawnTime.isBefore(LocalTime.now())) {
//                    String channelName = "test-" + pokemon.getName().toLowerCase() + "-" + location.toLowerCase();
//                    TextChannel raidChannel = (TextChannel) raidCategory.createTextChannel(channelName).submit()
//                            .get();
//                    LOG.info("created channel {}", raidChannel.getId());
//                    builder.append("\nCoordinate in channel: ").append(raidChannel.getAsMention());
//
//                    JobDataMap deleteChannelJobDataMap = new JobDataMap();
//                    deleteChannelJobDataMap.put("channel", raidChannel);
//                    JobDetail deleteChannelJobDetail = JobBuilder.newJob(DeleteChannelJob.class).
//                            setJobData(deleteChannelJobDataMap).build();
//
//                    LocalTime deleteChannelAt = despawnTime.plusMinutes(5);
//                    ZonedDateTime startAt = ZonedDateTime.of(LocalDate.now(), deleteChannelAt, ZoneId
//                            .systemDefault());
//
//                    raidChannel.sendMessage("This channel will be automatically deleted at " + startAt).submit();
//                    LOG.info("raid channel {} scheduled to be deleted at {}", channelName, startAt);
//
//                    Trigger trigger = TriggerBuilder.newTrigger().startAt(Date.from(startAt.toInstant())).build();
//                    scheduler.scheduleJob(deleteChannelJobDetail, trigger);
//
//                    Message reply = builder.build();
//                    Message sent = textChannel.sendMessage(reply).submit().get();
//
//                    JobDataMap deleteMessageJobDataMap = new JobDataMap();
//                    deleteMessageJobDataMap.put("message", sent);
//                    JobDetail deleteMessageJobDetail = JobBuilder.newJob(DeleteMessageJob.class).
//                            setJobData(deleteMessageJobDataMap).build();
//                    scheduler.scheduleJob(deleteMessageJobDetail, trigger);
//
//                } else {
//                    builder.append("\nDespawn is in the past, suppressing creation of raid channel.");
//                    Message reply = builder.build();
//                    textChannel.sendMessage(reply).submit();
//                }
//            } catch (InterruptedException e) {
//                LOG.error("todo", e);
//            } catch (ExecutionException e) {
//                LOG.error("todo", e);
//            } catch (SchedulerException e) {
//                LOG.error("todo", e);
//            }
//        }
//    }
//
//    private Optional<AnnotateImageResponse> performOCR(Attachment attachment) {
//        String attachmentId = attachment.getId();
//        LOG.info("fetching attachment {}", attachmentId);
//        List<AnnotateImageRequest> requests;
//        try (InputStream inputStream = attachment.getInputStream()) {
//            byte[] bytes = ByteStreams.toByteArray(inputStream);
//            ByteString imgBytes = ByteString.copyFrom(bytes);
//            Image img = Image.newBuilder().setContent(imgBytes).build();
//            Feature feat = Feature.newBuilder().setType(DOCUMENT_TEXT_DETECTION).build();
//            AnnotateImageRequest request = AnnotateImageRequest.newBuilder().
//                    addFeatures(feat).
//                    setImage(img).
//                    build();
//            requests = Collections.singletonList(request);
//        } catch (IOException e) {
//            LOG.error("exception fetching attachment " + attachmentId + " from discord", e);
//            return Optional.empty();
//        }
//
//        BatchAnnotateImagesResponse batchResponse = vision.batchAnnotateImages(requests);
//        List<AnnotateImageResponse> responses = batchResponse.getResponsesList();
//        AnnotateImageResponse response = responses.get(0);
//        if (response.hasError()) {
//            LOG.warn("error processing attachment: {} - {}", attachment.getId(), response.getError()
//                    .getMessage());
//            return Optional.empty();
//        } else {
//            return Optional.of(response);
//        }
//    }
//
//    private Optional<Pokemon> findPokemon(List<EntityAnnotation> entityAnnotations) {
//        for (EntityAnnotation entityAnnotation : entityAnnotations) {
//            String text = entityAnnotation.getDescription();
//            Optional<Pokemon> pokemon = pokemonRegistry.getPokemonByName(text);
//            if (pokemon.isPresent()) {
//                return pokemon;
//            }
//        }
//        return Optional.empty();
//    }
}
